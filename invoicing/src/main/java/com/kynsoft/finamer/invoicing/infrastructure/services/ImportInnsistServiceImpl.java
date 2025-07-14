package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistKafka;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistRoomRateKafka;
import com.kynsoft.finamer.invoicing.application.excel.ValidatorFactory;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.*;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.excel.event.ImportBookingProcessEvent;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageEmployeeReadDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportCacheRedisRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;

@Service
public class ImportInnsistServiceImpl {

    private final IManageAgencyService manageAgencyService;
    private final IManageHotelService manageHotelService;

    private final BookingImportCacheRedisRepository repository;
    private final IBookingImportHelperService bookingImportHelperService;
    private final ValidatorFactory<BookingRow> validatorFactory;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ManageEmployeeReadDataJPARepository employeeReadDataJPARepository;

    public ImportInnsistServiceImpl(IManageAgencyService manageAgencyService,
            IManageHotelService manageHotelService,
            BookingImportCacheRedisRepository repository,
            IBookingImportHelperService bookingImportHelperService,
            ValidatorFactory<BookingRow> validatorFactory,
            ApplicationEventPublisher applicationEventPublisher, ManageEmployeeReadDataJPARepository employeeReadDataJPARepository) {
        this.manageAgencyService = manageAgencyService;
        this.manageHotelService = manageHotelService;
        this.repository = repository;
        this.bookingImportHelperService = bookingImportHelperService;
        this.validatorFactory = validatorFactory;
        this.applicationEventPublisher = applicationEventPublisher;
        this.employeeReadDataJPARepository = employeeReadDataJPARepository;
    }

    private void createCache(ImportInnsistKafka request) {
        try {
            //Genera el log del proceso en redis al inicio de todo.
            BookingImportProcessDto start = BookingImportProcessDto.builder().importProcessId(request.getImportInnsitProcessId().toString())
                    .status(EProcessStatus.RUNNING)
                    .total(0)
                    .build();
            applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, start));

            UUID insistImportProcessId = UUID.randomUUID();
            //Construye el objeto usado en el excel y guarda en cache.
            List<BookingImportCache> bookingImportCacheList = this.create(request.getImportList(), request.getImportInnsitProcessId(), insistImportProcessId);

            //Obtengo de cache
            //List<BookingImportCache> list = this.repository.findAllByImportProcessId(insistImportProcessId.toString());
            boolean validateInsist = validatorFactory.validateInsist(bookingImportCacheList);
            validatorFactory.createValidators(EImportType.INNSIST.name());

            int rowNumber = 0;
            boolean stopImportProcess = false;

            List<UUID> agencies = this.employeeReadDataJPARepository.findAgencyIdsByEmployeeId(UUID.fromString(request.getEmployee()));
            List<UUID> hotels = this.employeeReadDataJPARepository.findHotelsIdsByEmployeeId(UUID.fromString(request.getEmployee()));

            for (BookingImportCache bookingImportCache : bookingImportCacheList) {
                BookingRow row = bookingImportCache.toAggregateImportInsist();
                row.setRowNumber(rowNumber);
                row.setImportProcessId(row.getInsistImportProcessId());
                row.setAgencies(agencies);
                row.setHotels(hotels);
                if (!validatorFactory.validate(row)) {
                    stopImportProcess = true;
                }
                rowNumber++;
            }

            validatorFactory.removeValidators();
            if (validateInsist && !stopImportProcess) {
                this.bookingImportHelperService.createInvoiceGroupingByCoupon(insistImportProcessId.toString(), request.getEmployee(), true);
                this.bookingImportHelperService.createInvoiceGroupingByBooking(insistImportProcessId.toString(), request.getEmployee(), true);
            }
            bookingImportCacheList.clear();
            BookingImportProcessDto end = BookingImportProcessDto.builder().importProcessId(request.getImportInnsitProcessId().toString())
                    .status(EProcessStatus.FINISHED)
                    .total(bookingImportCacheList.size())
                    .build();
            applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, end));
            bookingImportHelperService.removeAllImportCache(request.getImportInnsitProcessId().toString());
            bookingImportHelperService.removeAllImportCache(insistImportProcessId.toString());
            this.clearCache();

        } catch (Exception e) {
            BookingImportProcessDto bookingImportProcessDto = BookingImportProcessDto.builder().importProcessId(request.getImportInnsitProcessId().toString())
                    .hasError(true)
                    .exceptionMessage(e.getMessage())
                    .status(EProcessStatus.FINISHED)
                    .total(0)
                    .build();
            applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, bookingImportProcessDto));
            this.clearCache();
        }
    }

    private List<BookingImportCache> create(List<ImportInnsistBookingKafka> bookingRowList, UUID importInnsitProcessId, UUID insistImportProcessId) {
        List<BookingImportCache> bookingImportCacheList = new ArrayList<>();

        List<String> agencyCodes = bookingRowList.stream()
                .filter(importRow -> Objects.nonNull(importRow.getManageAgencyCode()) && !importRow.getManageAgencyCode().isEmpty())
                .map(ImportInnsistBookingKafka::getManageAgencyCode).toList();
        Map<String, ManageAgencyDto> agencyMap = this.getAgencyMap(agencyCodes);

        for (ImportInnsistBookingKafka importInnsistBookingKafka : bookingRowList) {
            for (ImportInnsistRoomRateKafka roomRate : importInnsistBookingKafka.getRoomRates()) {
                BookingRow bookingRow = new BookingRow();
                bookingRow.setImportProcessId(insistImportProcessId.toString());// este es para escribir en redis.
                bookingRow.setInsistImportProcessBookingId(importInnsistBookingKafka.getId().toString());
                bookingRow.setInsistImportProcessId(importInnsitProcessId.toString());//Este es el que le tengo que dar al flujo de validacion. que viene en la request.
                bookingRow.setTransactionDate(importInnsistBookingKafka.getInvoiceDate()
                         .toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                bookingRow.setManageHotelCode(importInnsistBookingKafka.getManageHotelCode());
                bookingRow.setManageAgencyCode(importInnsistBookingKafka.getManageAgencyCode());
                bookingRow.setFirstName(importInnsistBookingKafka.getFirstName());
                bookingRow.setLastName(importInnsistBookingKafka.getLastName());
                bookingRow.setCoupon(importInnsistBookingKafka.getCouponNumber());
                bookingRow.setHotelBookingNumber(importInnsistBookingKafka.getHotelBookingNumber());
                bookingRow.setRoomType(importInnsistBookingKafka.getRoomTypeCode());
                bookingRow.setRatePlan(importInnsistBookingKafka.getRatePlanCode());
                bookingRow.setHotelInvoiceNumber(importInnsistBookingKafka.getHotelInvoiceNumber().toString());
                            bookingRow.setBookingDate(importInnsistBookingKafka.getBookingDate()
                                    .toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                bookingRow.setNightType(importInnsistBookingKafka.getNightTypeCode());

                //Rate
                bookingRow.setCheckIn(roomRate.getCheckIn().toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                bookingRow.setCheckOut(roomRate.getCheckOut().toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                bookingRow.setNights(roomRate.getNights().doubleValue());
                bookingRow.setAdults(roomRate.getAdults().doubleValue());
                bookingRow.setChildren(roomRate.getChildren().doubleValue());
                bookingRow.setInvoiceAmount(roomRate.getInvoiceAmount());
                bookingRow.setRemarks(roomRate.getRemark());
                bookingRow.setAmountPAX(roomRate.getAdults().doubleValue() + roomRate.getChildren().doubleValue());
                bookingRow.setRoomNumber(roomRate.getRoomNumber());
                bookingRow.setHotelInvoiceAmount(roomRate.getHotelAmount());

                ManageAgencyDto agencyDto = agencyMap.get(bookingRow.getManageAgencyCode());
                BookingImportCache bookingImportCache = this.bookingImportHelperService.saveCachingImportBooking(bookingRow, agencyDto);
                bookingImportCacheList.add(bookingImportCache);
            }
        }
        return bookingImportCacheList;
    }

    private Map<String, ManageAgencyDto> getAgencyMap(List<String> agencyCodes){
        return this.manageAgencyService.getMapByCode(agencyCodes);
    }

    private void clearCache() {
        this.manageHotelService.clearManageHotelCache();
        this.manageAgencyService.clearManageHotelCache();
    }

    @Async
    public void createInvoiceFromGroupedBooking(ImportInnsistKafka request) {
        this.createCache(request);
    }
}