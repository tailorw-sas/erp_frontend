package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.ExcelException;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.importbooking.ImportBookingFromFileRequest;
import com.kynsoft.finamer.invoicing.application.excel.ValidatorFactory;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking.ImportBookingErrorRequest;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking.ImportBookingProcessStatusRequest;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking.ImportBookingProcessStatusResponse;
import com.kynsoft.finamer.invoicing.domain.dto.BookingImportProcessDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.domain.excel.ImportBookingRequest;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.excel.event.ImportBookingProcessEvent;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingRowError;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportProcessRedisEntity;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageEmployeeReadDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportProcessRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportRowErrorRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.utils.ImportDataCache;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BookingServiceImpl implements ImportBookingService {

    private final ValidatorFactory<BookingRow> validatorFactory;

    private final BookingImportProcessRedisRepository bookingImportProcessRedisRepository;

    private final BookingImportRowErrorRedisRepository bookingImportRowErrorRedisRepository;

    private final IBookingImportHelperService bookingImportHelperService;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final IManageHotelService hotelService;
    private final IManageAgencyService agencyService;
    private final IManageRoomTypeService roomTypeService;
    private final IManageRatePlanService ratePlanService;
    private final IInvoiceCloseOperationService closeOperationService;
    private final IManageNightTypeService nightTypeService;
    private final ManageEmployeeReadDataJPARepository employeeReadDataJPARepository;

    private final Lock redisLock = new ReentrantLock();
    private final Semaphore semaphore;

    public BookingServiceImpl(ValidatorFactory<BookingRow> validatorFactory,
                              BookingImportProcessRedisRepository bookingImportProcessRedisRepository,
                              BookingImportRowErrorRedisRepository bookingImportRowErrorRedisRepository,
                              IBookingImportHelperService bookingImportHelperService,
                              ApplicationEventPublisher applicationEventPublisher,
                              IManageHotelService hotelService,
                              IManageAgencyService agencyService,
                              IManageRoomTypeService roomTypeService,
                              IManageRatePlanService ratePlanService,
                              IInvoiceCloseOperationService closeOperationService,
                              IManageNightTypeService nightTypeService,
                              ManageEmployeeReadDataJPARepository employeeReadDataJPARepository
    ) {
        this.validatorFactory = validatorFactory;
        this.bookingImportProcessRedisRepository = bookingImportProcessRedisRepository;
        this.bookingImportRowErrorRedisRepository = bookingImportRowErrorRedisRepository;
        this.bookingImportHelperService = bookingImportHelperService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.roomTypeService = roomTypeService;
        this.ratePlanService = ratePlanService;
        this.closeOperationService = closeOperationService;
        this.nightTypeService = nightTypeService;
        this.semaphore = new Semaphore(1);
        this.hotelService = hotelService;
        this.agencyService = agencyService;
        this.employeeReadDataJPARepository = employeeReadDataJPARepository;
    }

    @Override
    @Async
    public void importBookingFromFile(ImportBookingFromFileRequest importBookingFromFileRequest) {
        String processId = importBookingFromFileRequest.getRequest().getImportProcessId();
        try {
            Logger.getLogger(BookingServiceImpl.class.getName()).log(Level.INFO, "*************Ingreso a procesar importacion de Excel. ID: " + processId + "*************");
            try {
                semaphore.acquire();
                try {
                    Logger.getLogger(BookingServiceImpl.class.getName()).log(Level.INFO, "*************Inicio procesamiento de importacion de Excel luego de semaforo. ID: " + processId + "*************");
                    ImportBookingRequest request = importBookingFromFileRequest.getRequest();
                    try {
                        ReaderConfiguration readerConfiguration = new ReaderConfiguration();
                        readerConfiguration.setIgnoreHeaders(true);
                        InputStream inputStream = new ByteArrayInputStream(request.getFile());
                        readerConfiguration.setInputStream(inputStream);
                        readerConfiguration.setReadLastActiveSheet(true);
                        ExcelBeanReader<BookingRow> reader = new ExcelBeanReader<>(readerConfiguration, BookingRow.class);
                        ExcelBean<BookingRow> excelBean = new ExcelBean<>(reader);

                        loadImportDataCache(excelBean, request.getEmployee());

                        validatorFactory.createValidators(request.getImportType().name());
                        BookingImportProcessDto start = BookingImportProcessDto.builder().importProcessId(request.getImportProcessId())
                                .status(EProcessStatus.RUNNING)
                                .total(0)
                                .build();
                        applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, start));
                        List<UUID> agencies = this.employeeReadDataJPARepository.findAgencyIdsByEmployeeId(UUID.fromString(request.getEmployee()));
                        List<UUID> hotels = this.employeeReadDataJPARepository.findHotelsIdsByEmployeeId(UUID.fromString(request.getEmployee()));
                        for (BookingRow bookingRow : excelBean) {
                            bookingRow.setImportProcessId(request.getImportProcessId());
                            bookingRow.setAgencies(agencies);
                            bookingRow.setHotels(hotels);
                            if (validatorFactory.validate(bookingRow)) {
                                bookingImportHelperService.groupAndCachingImportBooking(bookingRow,
                                        importBookingFromFileRequest.getRequest().getImportType());
                            }
                        }
                        validatorFactory.removeValidators();

                        bookingImportHelperService.createInvoiceFromGroupedBooking(request);
                        BookingImportProcessDto end = BookingImportProcessDto.builder().importProcessId(request.getImportProcessId())
                                .status(EProcessStatus.FINISHED)
                                .total(reader.totalRows())
                                .build();
                        applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, end));
                        bookingImportHelperService.removeAllImportCache(request.getImportProcessId());
                        this.clearCache();
                        Logger.getLogger(BookingServiceImpl.class.getName()).log(Level.INFO, "*************Fin de procesamiento de importacion de Excel. ID: " + processId + " *************");
                    } catch (BusinessException e) {
                        System.err.println("Error: " + e.getLocalizedMessage());
                        BookingImportProcessDto bookingImportProcessDto = BookingImportProcessDto.builder().importProcessId(request.getImportProcessId())
                                .hasError(true)
                                .exceptionMessage(e.getDetails())
                                .status(EProcessStatus.FINISHED)
                                .total(0)
                                .build();
                        applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, bookingImportProcessDto));
                        this.clearCache();
                        Logger.getLogger(BookingServiceImpl.class.getName()).log(Level.INFO, "*************Error en BusinessException de importacion de Excel. ID: " + processId + " *************" + "\n" + e);
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getLocalizedMessage());
                        BookingImportProcessDto bookingImportProcessDto = BookingImportProcessDto.builder().importProcessId(request.getImportProcessId())
                                .hasError(true)
                                .exceptionMessage(e.getMessage())
                                .status(EProcessStatus.FINISHED)
                                .total(0)
                                .build();
                        applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, bookingImportProcessDto));
                        this.clearCache();
                        Logger.getLogger(BookingServiceImpl.class.getName()).log(Level.INFO, "*************Error en Exception de importacion de Excel. ID: " + processId + " *************" + "\n" + e);
                    }
                } catch (Exception e) {
                    System.err.println("Errror ocurrido: " + e.getMessage());
                    System.err.println("Errror ocurrido: " + e.getCause().getLocalizedMessage());
                    this.clearCache();
                }
            } finally {
                semaphore.release();
                TimeUnit.SECONDS.sleep(5);
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(BookingServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadImportDataCache(ExcelBean<BookingRow> _excel_bean, String _employee) {
        //region searching hotels
        // 🔹 1️⃣ Extraer los códigos únicos de hoteles desde `ExcelBean`
        Set<String> hotelCodesInExcel = StreamSupport.stream(_excel_bean.spliterator(), false)
                .map(BookingRow::getManageHotelCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 🔹 2️⃣ Obtener los hoteles a los que el usuario tiene acceso
        Set<UUID> hotelsUserHasAccessTo = new HashSet<>(this.employeeReadDataJPARepository.findHotelsIdsByEmployeeId(UUID.fromString(_employee)));

        // 🔹 3️⃣ Filtrar los hoteles que aparecen en `ExcelBean` y que el usuario tiene acceso
        List<UUID> hotelsToSearch = hotelsUserHasAccessTo.stream()
                .filter(hotel -> hotelCodesInExcel.contains(hotel.toString())) // Mantener solo los que están en Excel
                .collect(Collectors.toList());

        // 🔹 4️⃣ Si después del filtrado no quedan hoteles, lanzar excepción
        if (hotelsToSearch.isEmpty()) {
            throw new BusinessException(DomainErrorMessage.HOTEL_ACCESS, "No hay hoteles disponibles en el Excel a los que el usuario tenga acceso.");
        }

        // 🔹 5️⃣ Buscar solo los hoteles permitidos en `hotelService`
        Map<String, ManageHotelDto> hotelDtoMap = hotelService.findByIds(hotelsToSearch)
                .stream()
                .collect(Collectors.toMap(ManageHotelDto::getCode, hotel -> hotel));

        //endregion

        //region searching agency

        // 🔹 1️⃣ Extraer los códigos únicos de agencias desde `ExcelBean`
        Set<String> agencyCodesInExcel = StreamSupport.stream(_excel_bean.spliterator(), false)
                .map(BookingRow::getManageAgencyCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 🔹 2️⃣ Obtener las agencias a las que el usuario tiene acceso
        Set<UUID> agenciesUserHasAccessTo = new HashSet<>(this.employeeReadDataJPARepository.findAgencyIdsByEmployeeId(UUID.fromString(_employee)));

        // 🔹 3️⃣ Filtrar las agencias que aparecen en `ExcelBean` y que el usuario tiene acceso
        List<UUID> agenciesToSearch = agenciesUserHasAccessTo.stream()
                .filter(agency -> agencyCodesInExcel.contains(agency.toString())) // Mantener solo las que están en Excel
                .toList();

        // 🔹 4️⃣ Si después del filtrado no quedan agencias, lanzar excepción
        if (agenciesToSearch.isEmpty()) {
            throw new BusinessException(DomainErrorMessage.AGENCY_ACCESS,
                    "No hay agencias disponibles en el Excel a las que el usuario tenga acceso.");
        }

        // 🔹 5️⃣ Buscar solo las agencias permitidas en `agencyService`
        Map<String, ManageAgencyDto> agencyDtoMap = agencyService.findByIds(agenciesToSearch)
                .stream()
                .collect(Collectors.toMap(ManageAgencyDto::getCode, agency -> agency));
        //endregion

        //region searching roomType
        Set<String> roomTypeCodesInExcel = StreamSupport.stream(_excel_bean.spliterator(), false)
                .map(BookingRow::getRoomType)
                .filter(Objects::nonNull)
                .filter(code -> !code.isEmpty())
                .collect(Collectors.toSet());

        Map<String, ManageRoomTypeDto> roomTypeDtoMap = Collections.emptyMap();

    // 🔹 3️⃣ Solo buscar si hay elementos en `roomTypeCodesInExcel`
        if (!roomTypeCodesInExcel.isEmpty()) {
            roomTypeDtoMap = roomTypeService.findByCodes(new ArrayList<>(roomTypeCodesInExcel))
                    .stream()
                    .collect(Collectors.toMap(ManageRoomTypeDto::getCode, roomType -> roomType));
        }

        //endregion

        //region searching ratePlan
        //endregion

        //region searching nightType
        //endregion
        //return new ImportDataCache(hotelDtoMap, agencyDtoMap, roomTypeDtoMap)
    }

    private void clearCache() {
        this.hotelService.clearManageHotelCache();
        this.agencyService.clearManageHotelCache();
    }

    @Override
    public PaginatedResponse getImportError(ImportBookingErrorRequest importBookingErrorRequest) {
        Page<BookingRowError> importErrorPages = bookingImportRowErrorRedisRepository.
                findAllByImportProcessId(importBookingErrorRequest.getImportProcessId(),
                        importBookingErrorRequest.getPageable());
        return new PaginatedResponse(
                importErrorPages.getContent().stream().sorted(Comparator.comparingInt(BookingRowError::getRowNumber)).collect(Collectors.toList()),
                //importErrorPages.getContent(),
                importErrorPages.getTotalPages(),
                importErrorPages.getNumberOfElements(),
                importErrorPages.getTotalElements(),
                importErrorPages.getSize(),
                importErrorPages.getNumber()
        );
    }

    @Override
    public ImportBookingProcessStatusResponse getImportBookingProcessStatus(ImportBookingProcessStatusRequest importBookingProcessStatusRequest) {
        BookingImportProcessDto statusDtp
                = bookingImportProcessRedisRepository.findByImportProcessId(importBookingProcessStatusRequest.getImportProcessId())
                .map(BookingImportProcessRedisEntity::toAgreggate).get();

        if (statusDtp.isHasError()) {
            throw new ExcelException(statusDtp.getExceptionMessage());
        }
        return new ImportBookingProcessStatusResponse(statusDtp.getStatus().name(), statusDtp.getTotal());

    }

}