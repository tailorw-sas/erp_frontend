package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.*;
import com.kynsoft.finamer.invoicing.domain.excel.ImportBookingRequest;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.bean.GroupBy;
import com.kynsoft.finamer.invoicing.domain.excel.bean.GroupByVirtualHotel;
import com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportCacheRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportRowErrorRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingImportHelperServiceImpl implements IBookingImportHelperService {

    private final IManageAgencyService agencyService;

    private final IManageHotelService manageHotelService;

    private final IManageInvoiceService invoiceService;
    private final IManageRatePlanService ratePlanService;

    private final IManageRoomTypeService roomTypeService;

    private final IManageNightTypeService nightTypeService;

    private final BookingImportCacheRedisRepository repository;

    private final BookingImportRowErrorRedisRepository errorRedisRepository;

    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;

    private final IManageInvoiceStatusService manageInvoiceStatusService;
    private final IManageInvoiceTypeService iManageInvoiceTypeService;
    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;

    public BookingImportHelperServiceImpl(IManageAgencyService agencyService,
                                          IManageHotelService manageHotelService,
                                          IManageInvoiceService invoiceService,
                                          IManageRatePlanService ratePlanService,
                                          IManageRoomTypeService roomTypeService, IManageNightTypeService nightTypeService,
                                          BookingImportCacheRedisRepository repository,
                                          BookingImportRowErrorRedisRepository errorRedisRepository,
                                          ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService,
                                          IManageInvoiceStatusService manageInvoiceStatusService,
                                          IManageInvoiceTypeService iManageInvoiceTypeService,
                                          IInvoiceStatusHistoryService invoiceStatusHistoryService) {
        this.agencyService = agencyService;
        this.manageHotelService = manageHotelService;
        this.invoiceService = invoiceService;
        this.ratePlanService = ratePlanService;
        this.roomTypeService = roomTypeService;
        this.nightTypeService = nightTypeService;
        this.repository = repository;
        this.errorRedisRepository = errorRedisRepository;
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
        this.iManageInvoiceTypeService = iManageInvoiceTypeService;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
    }

    @Override
    public void groupAndCachingImportBooking(BookingRow bookingRow, EImportType importType) {
        ManageAgencyDto manageAgencyDto = agencyService.findByCode(bookingRow.getManageAgencyCode());
        this.createCache(bookingRow, manageAgencyDto.getGenerationType().name());
    }

    @Override
    public void createInvoiceFromGroupedBooking(ImportBookingRequest request) {
        if (!errorRedisRepository.existsByImportProcessId(request.getImportProcessId())) {
            if (EImportType.VIRTUAL.equals(request.getImportType())) {
                createInvoiceGroupingForVirtualHotel(request.getImportProcessId(), request.getEmployee());
            } else {
                this.createInvoiceGroupingByCoupon(request.getImportProcessId(), request.getEmployee());
                this.createInvoiceGroupingByBooking(request.getImportProcessId(), request.getEmployee());
            }
        }
    }

    @Override
    public void removeAllImportCache(String importProcessId) {
        List<BookingImportCache> cache = repository.findAllByImportProcessId(importProcessId);
        repository.deleteAll(cache);
    }

    @Override
    public boolean canImportRow(BookingRow bookingRow, EImportType importType) {
        if (!manageHotelService.existByCode(bookingRow.getManageHotelCode())) {
            //En caso de que el hotel no exista , retornamos que se puede importar,
            //para que los se muestren en los errores la ausencia del hotel
            return true;
        }
        ManageHotelDto hotelDto = manageHotelService.findByCode(bookingRow.getManageHotelCode());
        if (importType.equals(EImportType.VIRTUAL)) {
            return hotelDto.isVirtual();
        } else {
            return !hotelDto.isVirtual();
        }

    }

    private void createInvoiceGroupingForVirtualHotel(String importProcessId, String employee) {
        Map<GroupByVirtualHotel, List<BookingRow>> grouped;
        List<BookingImportCache> importList = repository.findAllByImportProcessId(importProcessId);
        grouped = importList.stream().map(BookingImportCache::toAggregate).collect(Collectors.groupingBy(
                booking -> new GroupByVirtualHotel(
                        booking.getManageAgencyCode(),
                        booking.getManageHotelCode(),
                        Long.valueOf(booking.getHotelInvoiceNumber())
                )
        ));
        if (!grouped.isEmpty()) {
            grouped.forEach((key, value) -> {
                ManageAgencyDto agency = agencyService.findByCode(key.getAgency());
                ManageHotelDto hotel = manageHotelService.findByCode(key.getHotel());
                this.createInvoiceWithBooking(agency, hotel, value, employee);

            });
        }
    }

    private void createInvoiceGroupingByCoupon(String importProcessId, String employee) {
        Map<GroupBy, List<BookingRow>> grouped;
        List<BookingImportCache> bookingImportCacheStream = repository.findAllByGenerationTypeAndImportProcessId(EGenerationType.ByCoupon.name(), importProcessId);
        grouped = bookingImportCacheStream.stream().map(BookingImportCache::toAggregate)
                .collect(Collectors.groupingBy(bookingRow ->
                        new GroupBy(bookingRow.getManageAgencyCode(), bookingRow.getManageHotelCode(), bookingRow.getCoupon()))
                );

        if (!grouped.isEmpty()) {
            grouped.forEach((key, value) -> {
                ManageAgencyDto agency = agencyService.findByCode(key.getAgency());
                ManageHotelDto hotel = manageHotelService.findByCode(key.getHotel());
                this.createInvoiceWithBooking(agency, hotel, value, employee);

            });
        }
    }

    private void createInvoiceGroupingByBooking(String importProcessId, String employee) {
        List<BookingImportCache> bookingImportCacheStream = repository.findAllByGenerationTypeAndImportProcessId(EGenerationType.ByBooking.name(), importProcessId);
        bookingImportCacheStream.forEach(bookingImportCache -> {
            ManageAgencyDto agency = agencyService.findByCode(bookingImportCache.toAggregate().getManageAgencyCode());
            ManageHotelDto hotel = manageHotelService.findByCode(bookingImportCache.toAggregate().getManageHotelCode());
            this.createInvoiceWithBooking(agency, hotel, List.of(bookingImportCache.toAggregate()), employee);
        });
    }

    private void createInvoiceWithBooking(ManageAgencyDto agency, ManageHotelDto hotel, List<BookingRow> bookingRowList, String employee) {
        ManageInvoiceStatusDto invoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.PROCECSED);
        ManageInvoiceTypeDto invoiceTypeDto = this.iManageInvoiceTypeService.findByEInvoiceType(EInvoiceType.INVOICE);
        ManageInvoiceDto manageInvoiceDto = new ManageInvoiceDto();
        manageInvoiceDto.setAgency(agency);
        manageInvoiceDto.setHotel(hotel);
        manageInvoiceDto.setInvoiceType(EInvoiceType.INVOICE);
        manageInvoiceDto.setManageInvoiceType(invoiceTypeDto);
        manageInvoiceDto.setIsManual(false);
        manageInvoiceDto.setInvoiceDate(getInvoiceDate(bookingRowList.get(0)));
        manageInvoiceDto.setBookings(createBooking(bookingRowList));
        manageInvoiceDto.setId(UUID.randomUUID());
        manageInvoiceDto.setStatus(EInvoiceStatus.PROCECSED);
        manageInvoiceDto.setManageInvoiceStatus(invoiceStatus);
        manageInvoiceDto.setInvoiceAmount(calculateInvoiceAmount(bookingRowList));
        manageInvoiceDto.setDueAmount(calculateInvoiceAmount(bookingRowList));
        manageInvoiceDto.setOriginalAmount(manageInvoiceDto.getInvoiceAmount());
        if (hotel.isVirtual()) {
            manageInvoiceDto.setImportType(ImportType.BOOKING_FROM_FILE_VIRTUAL_HOTEL);
        } else {
            manageInvoiceDto.setImportType(ImportType.INVOICE_BOOKING_FROM_FILE);
        }
        manageInvoiceDto.setInvoiceNumber(createInvoiceNumber(hotel, bookingRowList.get(0)));
        manageInvoiceDto.setHotelInvoiceNumber(Long.valueOf(bookingRowList.get(0).getHotelInvoiceNumber()));
        manageInvoiceDto = invoiceService.create(manageInvoiceDto);
        this.createInvoiceHistory(manageInvoiceDto, employee);

        //TODO: aqui se envia a crear el invoice con sun booking en payment
        try {
            this.producerReplicateManageInvoiceService.create(manageInvoiceDto);
        } catch (Exception e) {
        }
    }

    private String createInvoiceNumber(ManageHotelDto hotel, BookingRow sample) {
        String invoiceNumber = InvoiceType.getInvoiceTypeCode(EInvoiceType.INVOICE);
        if (hotel.isVirtual()) {
            invoiceNumber += "-" + sample.getHotelInvoiceNumber();
        } else {
            if (hotel.getManageTradingCompanies() != null && hotel.getManageTradingCompanies().getIsApplyInvoice()) {
                invoiceNumber += "-" + hotel.getManageTradingCompanies().getCode();
            } else {
                invoiceNumber += "-" + hotel.getCode();
            }
        }
        return invoiceNumber;
    }

    private List<ManageBookingDto> createBooking(List<BookingRow> bookingRowList) {
        return bookingRowList.stream().map(bookingRow -> {
            ManageRatePlanDto ratePlanDto = Objects.nonNull(bookingRow.getRatePlan()) ?
                    ratePlanService.findByCode(bookingRow.getRatePlan()) : null;
            ManageRoomTypeDto roomTypeDto = Objects.nonNull(bookingRow.getRoomType()) ?
                    roomTypeService.findByCode(bookingRow.getRoomType()) : null;
            ManageNightTypeDto nightTypeDto = Objects.nonNull(bookingRow.getNightType()) ?
                    nightTypeService.findByCode(bookingRow.getNightType()) : null;
            ManageBookingDto bookingDto = bookingRow.toAggregate();
            bookingDto.setRatePlan(ratePlanDto);
            bookingDto.setRoomType(roomTypeDto);
            bookingDto.setId(UUID.randomUUID());
            bookingDto.setHotelCreationDate(DateUtil.parseDateToDateTime(bookingRow.getTransactionDate()));
            bookingDto.setRoomRates(List.of(createRoomRateDto(bookingRow)));
            bookingDto.setNightType(nightTypeDto);
            return bookingDto;
        }).toList();
    }

    private ManageRoomRateDto createRoomRateDto(BookingRow bookingRow) {
        ManageBookingDto bookingDto = bookingRow.toAggregate();
        ManageRoomRateDto manageRoomRateDto = new ManageRoomRateDto();
        manageRoomRateDto.setId(UUID.randomUUID());
        manageRoomRateDto.setAdults(bookingDto.getAdults());
        manageRoomRateDto.setChildren(bookingDto.getChildren());
        manageRoomRateDto.setCheckIn(bookingDto.getCheckIn());
        manageRoomRateDto.setCheckOut(bookingDto.getCheckOut());
        manageRoomRateDto.setHotelAmount(bookingDto.getHotelAmount());
        manageRoomRateDto.setNights(bookingDto.getNights());
        manageRoomRateDto.setRoomNumber(bookingDto.getRoomNumber());
        manageRoomRateDto.setInvoiceAmount(bookingDto.getInvoiceAmount());
        return manageRoomRateDto;
    }

    private double calculateInvoiceAmount(List<BookingRow> bookingRowList) {
        return bookingRowList.stream().mapToDouble(BookingRow::getInvoiceAmount).sum();
    }

    private LocalDateTime getInvoiceDate(BookingRow bookingRow) {
        LocalDateTime excelDate = DateUtil.parseDateToDateTime(bookingRow.getTransactionDate());
        LocalDateTime transactionDate = LocalDateTime.now();
        if (Objects.nonNull(bookingRow.getTransactionDate()) &&
                Objects.nonNull(excelDate) &&
                !LocalDate.now().isEqual(excelDate.toLocalDate())) {
            transactionDate = excelDate;
        }
        return transactionDate;
    }

    private void createCache(BookingRow bookingRow, String generationType) {
        BookingImportCache bookingImportCache = new BookingImportCache(bookingRow);
        bookingImportCache.setGenerationType(generationType);
        bookingImportCache.setImportProcessId(bookingRow.getImportProcessId());
        repository.save(bookingImportCache);
    }

    private void createInvoiceHistory(ManageInvoiceDto manageInvoice, String employee) {
        this.invoiceStatusHistoryService.create(
                new InvoiceStatusHistoryDto(
                        UUID.randomUUID(),
                        manageInvoice,
                        "The invoice data was inserted.",
                        LocalDateTime.now(),
                        employee,
                        EInvoiceStatus.PROCECSED
                )
        );
    }


}
