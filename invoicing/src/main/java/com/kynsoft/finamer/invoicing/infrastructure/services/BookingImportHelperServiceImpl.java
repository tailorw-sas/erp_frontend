package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.utils.ScaleAmount;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.*;
import com.kynsoft.finamer.invoicing.domain.excel.ImportBookingRequest;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.bean.GroupByCoupon;
import com.kynsoft.finamer.invoicing.domain.excel.bean.GroupByCouponHotelBookingNumber;
import com.kynsoft.finamer.invoicing.domain.excel.bean.GroupByHotelBookingNumber;
import com.kynsoft.finamer.invoicing.domain.excel.bean.GroupByVirtualHotel;
import com.kynsoft.finamer.invoicing.domain.excel.bean.GroupByVirtualHotelBookingNumber;
import com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportCacheRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportRowErrorRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private final IManageEmployeeService employeeService;

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
            IInvoiceStatusHistoryService invoiceStatusHistoryService, IManageEmployeeService employeeService) {
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
        this.employeeService = employeeService;
    }

    @Override
    public void groupAndCachingImportBooking(BookingRow bookingRow, EImportType importType) {
        ManageAgencyDto manageAgencyDto = agencyService.findByCode(bookingRow.getManageAgencyCode());
        this.createCache(bookingRow, manageAgencyDto.getGenerationType().name());
    }

    @Override
    public void saveCachingImportBooking(BookingRow bookingRow) {
        this.createCacheInsist(bookingRow);
    }

    @Override
    public void createInvoiceFromGroupedBooking(ImportBookingRequest request) {
        if (!errorRedisRepository.existsByImportProcessId(request.getImportProcessId())) {
            if (EImportType.VIRTUAL.equals(request.getImportType())) {
                createInvoiceGroupingForVirtualHotel(request.getImportProcessId(), request.getEmployee());
            } else {
                this.createInvoiceGroupingByCoupon(request.getImportProcessId(), request.getEmployee(), false);
                this.createInvoiceGroupingByBooking(request.getImportProcessId(), request.getEmployee(), false);
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
        Map<GroupByVirtualHotel, List<BookingRow>> groupedByHotelBookingNumber;
        List<BookingImportCache> bookingImportCacheStream = repository.findAllByImportProcessId(importProcessId);
        Collections.sort(bookingImportCacheStream, Comparator.comparingInt(BookingImportCache::getRowNumber));

        groupedByHotelBookingNumber = bookingImportCacheStream.stream().map(BookingImportCache::toAggregate)
                .collect(Collectors.groupingBy(bookingRow
                        -> new GroupByVirtualHotel(
                        bookingRow.getTransactionDate(),
                        bookingRow.getManageAgencyCode(),
                        bookingRow.getManageHotelCode(),
                        Long.valueOf(bookingRow.getHotelInvoiceNumber())
                )));

        if (!groupedByHotelBookingNumber.isEmpty()) {
            groupedByHotelBookingNumber.forEach((key, value) -> {
                ManageAgencyDto agency = agencyService.findByCode(key.getAgency());
                ManageHotelDto hotel = manageHotelService.findByCode(key.getHotel());
                this.createInvoiceWithBooking(agency, hotel, value, employee, "HotelInvoiceNumber", false);
            });
        }
    }

    @Override
    public void createInvoiceGroupingByCoupon(String importProcessId, String employee, boolean insisit) {
        Map<GroupByCoupon, List<BookingRow>> groupedByHotelBookingNumber;
        List<BookingImportCache> bookingImportCacheStream = repository.findAllByGenerationTypeAndImportProcessId(EGenerationType.ByCoupon.name(), importProcessId);
        Collections.sort(bookingImportCacheStream, Comparator.comparingInt(BookingImportCache::getRowNumber));

        groupedByHotelBookingNumber = bookingImportCacheStream.stream().map(BookingImportCache::toAggregate)
                .collect(Collectors.groupingBy(bookingRow
                        -> new GroupByCoupon(
                        bookingRow.getTransactionDate(),
                        bookingRow.getManageAgencyCode(),
                        bookingRow.getManageHotelCode(),
                        bookingRow.getCoupon()
                )));

        if (!groupedByHotelBookingNumber.isEmpty()) {
            groupedByHotelBookingNumber.forEach((key, value) -> {
                ManageAgencyDto agency = agencyService.findByCode(key.getAgency());
                ManageHotelDto hotel = manageHotelService.findByCode(key.getHotel());
                this.createInvoiceWithBooking(agency, hotel, value, employee, "ByCoupon", insisit);
            });
        }
    }

    @Override
    public void createInvoiceGroupingByBooking(String importProcessId, String employee, boolean insisit) {
        /**
         * *
         * Para el caso de la agrupacion por Booking, al tener en una agrupacion
         * varios rates con el mismo Hotel Booking Number se crearian varios
         * Rates en un solo booking y ese booking seria una sola factura.
         */
        Map<GroupByHotelBookingNumber, List<BookingRow>> grouped;
        List<BookingImportCache> bookingImportCacheStream = repository.findAllByGenerationTypeAndImportProcessId(EGenerationType.ByBooking.name(), importProcessId);
        Collections.sort(bookingImportCacheStream, Comparator.comparingInt(BookingImportCache::getRowNumber));

        grouped = bookingImportCacheStream.stream().map(BookingImportCache::toAggregate)
                .collect(Collectors.groupingBy(bookingRow
                        -> new GroupByHotelBookingNumber(
                        bookingRow.getHotelBookingNumber(),
                        bookingRow.getManageAgencyCode(),
                        bookingRow.getManageHotelCode(),
                        bookingRow.getTransactionDate()
                )));

        List<Map.Entry<GroupByHotelBookingNumber, List<BookingRow>>> list = new ArrayList<>(grouped.entrySet());
        Collections.sort(list, Comparator.comparing(entry -> entry.getValue().get(0).getRowNumber()));

        Map<GroupByHotelBookingNumber, List<BookingRow>> orderedGrouped = new LinkedHashMap<>();
        for (Map.Entry<GroupByHotelBookingNumber, List<BookingRow>> entry : list) {
            orderedGrouped.put(entry.getKey(), entry.getValue());
        }

        if (!orderedGrouped.isEmpty()) {
            orderedGrouped.forEach((key, value) -> {
                ManageAgencyDto agency = agencyService.findByCode(key.getAgency());
                ManageHotelDto hotel = manageHotelService.findByCode(key.getHotel());
                this.createInvoiceWithBooking(agency, hotel, value, employee, "ByBooking", insisit);
            });
        }
    }

    private void createInvoiceWithBooking(ManageAgencyDto agency, ManageHotelDto hotel, List<BookingRow> bookingRowList, String employee, String groupType, boolean insisit) {
        ManageInvoiceStatusDto invoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.PROCECSED);
        ManageInvoiceTypeDto invoiceTypeDto = this.iManageInvoiceTypeService.findByEInvoiceType(EInvoiceType.INVOICE);
        ManageInvoiceDto manageInvoiceDto = new ManageInvoiceDto();
        manageInvoiceDto.setAgency(agency);
        manageInvoiceDto.setHotel(hotel);
        manageInvoiceDto.setInvoiceType(EInvoiceType.INVOICE);
        manageInvoiceDto.setManageInvoiceType(invoiceTypeDto);
        manageInvoiceDto.setIsManual(false);
        manageInvoiceDto.setInvoiceDate(getInvoiceDate(bookingRowList.get(0)));
        manageInvoiceDto.setBookings(createBooking(bookingRowList, hotel, groupType));
        manageInvoiceDto.setId(UUID.randomUUID());
        manageInvoiceDto.setStatus(EInvoiceStatus.PROCECSED);
        manageInvoiceDto.setManageInvoiceStatus(invoiceStatus);
        manageInvoiceDto.setInvoiceAmount(ScaleAmount.scaleAmount(calculateInvoiceAmount(bookingRowList)));
        manageInvoiceDto.setDueAmount(ScaleAmount.scaleAmount(calculateInvoiceAmount(bookingRowList)));
        manageInvoiceDto.setOriginalAmount(ScaleAmount.scaleAmount(manageInvoiceDto.getInvoiceAmount()));
        if (hotel.isVirtual()) {
            manageInvoiceDto.setImportType(ImportType.BOOKING_FROM_FILE_VIRTUAL_HOTEL);
        } else {
            manageInvoiceDto.setImportType(ImportType.INVOICE_BOOKING_FROM_FILE);
        }
        if (insisit) {
            manageInvoiceDto.setImportType(ImportType.INSIST);
        }
        manageInvoiceDto.setInvoiceNumber(createInvoiceNumber(hotel, bookingRowList.get(0)));
        manageInvoiceDto.setHotelInvoiceNumber(bookingRowList.get(0).getHotelInvoiceNumber() != null ? Long.valueOf(bookingRowList.get(0).getHotelInvoiceNumber()) : null);
        manageInvoiceDto = invoiceService.create(manageInvoiceDto);
        this.createInvoiceHistory(manageInvoiceDto, employee);

        //TODO: aqui se envia a crear el invoice con sun booking en payment
        try {
            this.producerReplicateManageInvoiceService.create(manageInvoiceDto, null, null);
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

    private ManageBookingDto createOneBooking(List<BookingRow> bookingRowList, ManageHotelDto hotel) {
        ManageRatePlanDto ratePlanDto = Objects.nonNull(bookingRowList.get(0).getRatePlan()) ? ratePlanService.findManageRatePlanByCodeAndHotelCode(bookingRowList.get(0).getRatePlan(), hotel.getCode()) : null;
        ManageRoomTypeDto roomTypeDto = Objects.nonNull(bookingRowList.get(0).getRoomType()) ? roomTypeService.findManageRoomTypenByCodeAndHotelCode(bookingRowList.get(0).getRoomType(), hotel.getCode()) : null;
        ManageNightTypeDto nightTypeDto = Objects.nonNull(bookingRowList.get(0).getNightType()) ? nightTypeService.findByCode(bookingRowList.get(0).getNightType()) : null;
        ManageBookingDto bookingDto = bookingRowList.get(0).toAggregate();
        bookingDto.setRatePlan(ratePlanDto);
        bookingDto.setRoomType(roomTypeDto);
        bookingDto.setId(UUID.randomUUID());
        List<ManageRoomRateDto> rates = new ArrayList<>();
        double bookingAmount = 0;
        double bookingHotelAmount = 0;
        for (BookingRow bookingRow : bookingRowList) {
            rates.add(this.createRoomRateDto(bookingRow));
            bookingAmount = bookingAmount + bookingRow.getInvoiceAmount();
            bookingHotelAmount = bookingHotelAmount + bookingRow.getHotelInvoiceAmount();
        }
        bookingDto.setInvoiceAmount(bookingAmount);
        bookingDto.setDueAmount(bookingAmount);
        bookingDto.setHotelAmount(bookingHotelAmount);
        bookingDto.setRoomRates(rates);
        bookingDto.setHotelCreationDate(DateUtil.parseDateToDateTime(bookingRowList.get(0).getTransactionDate()));
        bookingDto.setNightType(nightTypeDto);

        //Calculados
        this.calculateCheckinAndCheckout(bookingDto);
        this.calculateAdults(bookingDto);
        this.calculateChildren(bookingDto);
        this.calculateRateAdults(bookingDto);
        this.calculateRateChild(bookingDto);

        return bookingDto;
    }

    public void calculateRateChild(ManageBookingDto bookingDto) {

        double total = bookingDto.getRoomRates().stream()
                .mapToDouble(rate -> Optional.ofNullable(rate.getRateChild())
                .orElse(0.0))
                .sum();
        bookingDto.setRateChild(ScaleAmount.scaleAmount(total));
    }

    public void calculateRateAdults(ManageBookingDto bookingDto) {

        double total = bookingDto.getRoomRates().stream()
                .mapToDouble(rate -> Optional.ofNullable(rate.getRateAdult())
                .orElse(0.0))
                .sum();
        bookingDto.setRateAdult(ScaleAmount.scaleAmount(total));
    }

    public void calculateChildren(ManageBookingDto bookingDto) {
        Double max = bookingDto.getRoomRates().stream()
                .mapToDouble(ManageRoomRateDto::getChildren)
                .max()
                .orElse(0);

        bookingDto.setChildren(max.intValue());
    }

    public void calculateAdults(ManageBookingDto bookingDto) {

        Double max = bookingDto.getRoomRates().stream()
                .mapToDouble(ManageRoomRateDto::getAdults)
                .max()
                .orElse(0);
        bookingDto.setAdults(max.intValue());
    }

    public void calculateCheckinAndCheckout(ManageBookingDto bookingDto) {

        LocalDateTime checkIn = bookingDto.getRoomRates().stream()
                .map(ManageRoomRateDto::getCheckIn)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new IllegalStateException("No se encontró una fecha de entrada válida"));

        LocalDateTime checkOut = bookingDto.getRoomRates().stream()
                .map(ManageRoomRateDto::getCheckOut)
                .max(LocalDateTime::compareTo)
                .orElseThrow(() -> new IllegalStateException("No se encontró una fecha de salida válida"));

        bookingDto.setCheckIn(checkIn);
        bookingDto.setCheckOut(checkOut);
    }

    private Double calculateRateAdult(Double rateAmount, Long nights, Integer adults) {
        return adults == 0 ? 0.0 : rateAmount / (nights * adults);
    }

    private Double calculateRateChild(Double rateAmount, Long nights, Integer children) {
        return children == 0 ? 0.0 : rateAmount / (nights * children);
    }

    private List<ManageBookingDto> createBooking(List<BookingRow> bookingRowList, ManageHotelDto hotel, String groupType) {
        try {
            Collections.sort(bookingRowList, Comparator.comparingInt(BookingRow::getRowNumber));
        } catch (Exception e) {
        }

        if (groupType.equals("ByBooking")) {
            return List.of(this.createOneBooking(bookingRowList, hotel));
        }
        if (groupType.equals("ByCoupon")) {

            Map<GroupByCouponHotelBookingNumber, List<BookingRow>> grouped;
            grouped = bookingRowList.stream().collect(Collectors.groupingBy(bookingRow
                    -> new GroupByCouponHotelBookingNumber(
                            bookingRow.getHotelBookingNumber()
                    )
            ));

            List<Map.Entry<GroupByCouponHotelBookingNumber, List<BookingRow>>> list = new ArrayList<>(grouped.entrySet());
            try {
                Collections.sort(list, Comparator.comparing(entry -> entry.getValue().get(0).getRowNumber()));
            } catch (Exception e) {
            }

            Map<GroupByCouponHotelBookingNumber, List<BookingRow>> orderedGrouped = new LinkedHashMap<>();
            for (Map.Entry<GroupByCouponHotelBookingNumber, List<BookingRow>> entry : list) {
                orderedGrouped.put(entry.getKey(), entry.getValue());
            }

            List<ManageBookingDto> bookingDtos = new ArrayList<>();
            orderedGrouped.forEach((key, value) -> {
                bookingDtos.add(this.createOneBooking(value, hotel));
            });
            return bookingDtos;
        }
        if (groupType.equals("HotelInvoiceNumber")) {
            Map<GroupByVirtualHotelBookingNumber, List<BookingRow>> grouped;
            grouped = bookingRowList.stream().collect(Collectors.groupingBy(bookingRow
                    -> new GroupByVirtualHotelBookingNumber(
                            bookingRow.getHotelBookingNumber()
                    )
            ));

            List<Map.Entry<GroupByVirtualHotelBookingNumber, List<BookingRow>>> list = new ArrayList<>(grouped.entrySet());
            try {
                Collections.sort(list, Comparator.comparing(entry -> entry.getValue().get(0).getRowNumber()));
            } catch (Exception e) {
            }

            Map<GroupByVirtualHotelBookingNumber, List<BookingRow>> orderedGrouped = new LinkedHashMap<>();
            for (Map.Entry<GroupByVirtualHotelBookingNumber, List<BookingRow>> entry : list) {
                orderedGrouped.put(entry.getKey(), entry.getValue());
            }
            List<ManageBookingDto> bookingDtos = new ArrayList<>();
            orderedGrouped.forEach((key, value) -> {
                bookingDtos.add(this.createOneBooking(value, hotel));
            });
            return bookingDtos;
        }
        return List.of();
    }

    private ManageRoomRateDto createRoomRateDto(BookingRow bookingRow) {
        ManageBookingDto bookingDto = bookingRow.toAggregate();
        ManageRoomRateDto manageRoomRateDto = new ManageRoomRateDto();
        manageRoomRateDto.setId(UUID.randomUUID());
        manageRoomRateDto.setAdults(bookingDto.getAdults());
        manageRoomRateDto.setChildren(bookingDto.getChildren());
        manageRoomRateDto.setCheckIn(bookingDto.getCheckIn());
        manageRoomRateDto.setCheckOut(bookingDto.getCheckOut());
        manageRoomRateDto.setHotelAmount(ScaleAmount.scaleAmount(bookingDto.getHotelAmount()));
        manageRoomRateDto.setNights(bookingDto.getNights());
        manageRoomRateDto.setRoomNumber(bookingDto.getRoomNumber());
        manageRoomRateDto.setInvoiceAmount(ScaleAmount.scaleAmount(bookingDto.getInvoiceAmount()));

        manageRoomRateDto.setRateAdult(this.calculateRateAdult(manageRoomRateDto.getInvoiceAmount(), bookingDto.getNights(), bookingDto.getAdults()));
        manageRoomRateDto.setRateChild(this.calculateRateChild(manageRoomRateDto.getInvoiceAmount(), bookingDto.getNights(), bookingDto.getChildren()));
        return manageRoomRateDto;
    }

    private double calculateInvoiceAmount(List<BookingRow> bookingRowList) {
        return bookingRowList.stream().mapToDouble(BookingRow::getInvoiceAmount).sum();
    }

    private LocalDateTime getInvoiceDate(BookingRow bookingRow) {
        LocalDateTime excelDate = DateUtil.parseDateToDateTime(bookingRow.getTransactionDate());
        LocalDateTime transactionDate = LocalDateTime.now();
        if (Objects.nonNull(bookingRow.getTransactionDate())
                && Objects.nonNull(excelDate)
                && !LocalDate.now().isEqual(excelDate.toLocalDate())) {
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

    private void createCacheInsist(BookingRow bookingRow) {
        BookingImportCache bookingImportCache = new BookingImportCache(bookingRow);
        bookingImportCache.setInsistImportProcessId(bookingRow.getInsistImportProcessId());
        bookingImportCache.setImportProcessId(bookingRow.getImportProcessId());
        bookingImportCache.setInsistImportProcessBookingId(bookingRow.getInsistImportProcessBookingId());
        repository.save(bookingImportCache);
    }

    private void createInvoiceHistory(ManageInvoiceDto manageInvoice, String employee) {
        this.invoiceStatusHistoryService.create(
                new InvoiceStatusHistoryDto(
                        UUID.randomUUID(),
                        manageInvoice,
                        "The invoice data was inserted.",
                        LocalDateTime.now(),
                        this.employeeService.getEmployeeFullName(employee),
                        EInvoiceStatus.PROCECSED,
                        0L
                )
        );
    }

}
