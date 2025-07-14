package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.utils.BankerRounding;
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
import com.kynsoft.finamer.invoicing.infrastructure.utils.InvoiceUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
        //TODO Validar o quitar
        ManageAgencyDto manageAgencyDto = agencyService.findByCode(bookingRow.getManageAgencyCode());
        this.createCache(bookingRow, manageAgencyDto.getGenerationType().name());
    }

    @Override
    public BookingImportCache saveCachingImportBooking(BookingRow bookingRow, ManageAgencyDto agencyDto) {
        return this.createCacheInsist(bookingRow, agencyDto);
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
        Map<GroupByVirtualHotel, List<BookingRow>> groupedByHotelInvoiceNumber;
        List<BookingImportCache> bookingImportCacheStream = repository.findAllByImportProcessId(importProcessId);
        Collections.sort(bookingImportCacheStream, Comparator.comparingInt(BookingImportCache::getRowNumber));

        groupedByHotelInvoiceNumber = bookingImportCacheStream.stream().map(BookingImportCache::toAggregate)
                .collect(Collectors.groupingBy(bookingRow
                        -> new GroupByVirtualHotel(
                        bookingRow.getTransactionDate(),
                        bookingRow.getManageAgencyCode(),
                        bookingRow.getManageHotelCode(),
                        Long.valueOf(bookingRow.getHotelInvoiceNumber())
                )));

        //agrupando y comprobando el booking number
        Map<GroupByHotelBookingNumber, List<BookingRow>> groupedByHotelBookingNumber;
        groupedByHotelBookingNumber = groupByHotelBookingNumberListMap(bookingImportCacheStream);
        if (!groupedByHotelBookingNumber.isEmpty()) {
            String checkBookingNumberRepeated = checkForDuplicateHotelBookingNumbers(groupedByHotelBookingNumber);
            if (!checkBookingNumberRepeated.isEmpty()){
                throw new BusinessException(
                        DomainErrorMessage.HOTEL_BOOKING_NUMBER_REPEATED,
                        "Hotel Booking Number: " + checkBookingNumberRepeated +"is repeated in the uploaded document."
                );
            }
        }

        if (!groupedByHotelInvoiceNumber.isEmpty()) {
            String checkInvoiceNumberRepeated = checkForDuplicateHotelInvoiceNumbers(groupedByHotelInvoiceNumber);
            if (!checkInvoiceNumberRepeated.isEmpty()){
                throw new BusinessException(
                        DomainErrorMessage.HOTEL_INVOICE_NUMBER_REPEATED,
                        "Hotel Invoice Number: " + checkInvoiceNumberRepeated +"is repeated in the uploaded document."
                );
            }

            //validando la cantidad de adultos en los booking agrupados
            groupedByHotelInvoiceNumber.forEach((key, value) -> cantAdultsValid(value));

            groupedByHotelInvoiceNumber.forEach((key, value) -> {
                ManageAgencyDto agency = agencyService.findByCode(key.getAgency());
                ManageHotelDto hotel = manageHotelService.findByCode(key.getHotel());
                this.createInvoiceWithBooking(agency, hotel, value, employee, "HotelInvoiceNumber", false);
            });
        }
    }

    private String checkForDuplicateHotelInvoiceNumbers(Map<GroupByVirtualHotel, List<BookingRow>> groupedByHotelBookingNumber) {
        String resp = "";
        // Crear un mapa auxiliar para agrupar por hotelInvoiceNumber
        Map<Long, List<String>> invoiceNumberMap = new HashMap<>();

        // Recorrer las claves del mapa original
        for (GroupByVirtualHotel key : groupedByHotelBookingNumber.keySet()) {
            Long hotelInvoiceNumber = key.getHotelInvoiceNumber();

            // Agregar la clave al mapa auxiliar
            invoiceNumberMap.computeIfAbsent(hotelInvoiceNumber, k -> new ArrayList<>()).add(key.getHotel());
        }

        // Verificar si hay algún hotelInvoiceNumber con más de una clave y hotel repetido
        for (Map.Entry<Long, List<String>> entry : invoiceNumberMap.entrySet()) {
            if (entry.getValue().size() > 1 && InvoiceUtils.hasDuplicates(entry.getValue())) {
                resp = resp.concat(entry.getKey().toString() + " ");
            }
        }

        return resp; // No hay duplicados
    }

    private String checkForDuplicateHotelBookingNumbers(Map<GroupByHotelBookingNumber, List<BookingRow>> groupedByHotelBookingNumber) {
        String resp = "";
        // Crear un mapa auxiliar para agrupar por hotelInvoiceNumber
        Map<String, List<String>> invoiceNumberMap = new HashMap<>();

        // Recorrer las claves del mapa original
        for (GroupByHotelBookingNumber key : groupedByHotelBookingNumber.keySet()) {
            String hotelBookingNumber = key.getHotelBookingNumber();

            // Agregar la clave al mapa auxiliar
            invoiceNumberMap.computeIfAbsent(hotelBookingNumber, k -> new ArrayList<>()).add(key.getHotel());
        }

        // Verificar si hay algún hotelInvoiceNumber con más de una clave y hotel repetido
        for (Map.Entry<String, List<String>> entry : invoiceNumberMap.entrySet()) {
            if (entry.getValue().size() > 1 && InvoiceUtils.hasDuplicates(entry.getValue())) {
                resp = resp.concat(entry.getKey().toString() + " ");
            }
        }

        return resp; // No hay duplicados
    }

    private Map<GroupByHotelBookingNumber, List<BookingRow>> groupByHotelBookingNumberListMap(List<BookingImportCache> bookingImportCacheStream){
        Map<GroupByHotelBookingNumber, List<BookingRow>> groupedByHotelBookingNumber;
        List<BookingImportCache> modifiedList = bookingImportCacheStream.stream()
                .map(booking -> {
                    BookingImportCache copy = new BookingImportCache();
                    BeanUtils.copyProperties(booking, copy); // Copia las propiedades
                    copy.setHotelBookingNumber(InvoiceUtils.removeBlankSpaces(booking.getHotelBookingNumber()));
                    return copy;
                })
                .collect(Collectors.toList());

        groupedByHotelBookingNumber = modifiedList.stream().map(BookingImportCache::toAggregate)
                .collect(Collectors.groupingBy(bookingRow
                        -> new GroupByHotelBookingNumber(
                        bookingRow.getHotelBookingNumber(),
                        bookingRow.getManageAgencyCode(),
                        bookingRow.getManageHotelCode(),
                        bookingRow.getTransactionDate())
                ));
        return groupedByHotelBookingNumber;
    }

    @Override
    public void createInvoiceGroupingByCoupon(String importProcessId, String employee, boolean innsist) {
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

            //validando la cantidad de adultos en los booking agrupados
            groupedByHotelBookingNumber.forEach((key, value) -> cantAdultsValid(value));

            groupedByHotelBookingNumber.forEach((key, value) -> {
                ManageAgencyDto agency = agencyService.findByCode(key.getAgency());
                ManageHotelDto hotel = manageHotelService.findByCode(key.getHotel());
                this.createInvoiceWithBooking(agency, hotel, value, employee, "ByCoupon", innsist);
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

            //validando la cantidad de adultos en los booking agrupados
            orderedGrouped.forEach((key, value) -> cantAdultsValid(value));

            orderedGrouped.forEach((key, value) -> {
                ManageAgencyDto agency = agencyService.findByCode(key.getAgency());
                ManageHotelDto hotel = manageHotelService.findByCode(key.getHotel());
                this.createInvoiceWithBooking(agency, hotel, value, employee, "ByBooking", insisit);
            });
        }
    }

    private void createInvoiceWithBooking(ManageAgencyDto agency, ManageHotelDto hotel, List<BookingRow> bookingRowList,
                                          String employee, String groupType, boolean innsist) {
        //TODO - Mejorar todo este proceso
        ManageInvoiceStatusDto invoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.PROCESSED);
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
        manageInvoiceDto.setStatus(EInvoiceStatus.PROCESSED);
        manageInvoiceDto.setManageInvoiceStatus(invoiceStatus);
        double invoiceAmount = BankerRounding.round(calculateInvoiceAmount(bookingRowList));
        manageInvoiceDto.setInvoiceAmount(invoiceAmount);
        manageInvoiceDto.setDueAmount(invoiceAmount);
        manageInvoiceDto.setOriginalAmount(invoiceAmount);
        if (hotel.isVirtual()) {
            manageInvoiceDto.setImportType(ImportType.BOOKING_FROM_FILE_VIRTUAL_HOTEL);
        } else {
            manageInvoiceDto.setImportType(ImportType.INVOICE_BOOKING_FROM_FILE);
        }
        if (innsist) {
            manageInvoiceDto.setImportType(ImportType.INSIST);
        }
        manageInvoiceDto.setInvoiceNumber(createInvoiceNumber(hotel, bookingRowList.get(0)));
        manageInvoiceDto.setHotelInvoiceNumber(bookingRowList.get(0).getHotelInvoiceNumber() != null ? Long.valueOf(bookingRowList.get(0).getHotelInvoiceNumber()) : null);
        //TODO Eliminar esto y devolver el manageInvoiceDto antes de crear para garantizar transaccionalidad
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
        //TODO Mejorar este proceso (Cargar en memoria los catalogos)
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
            bookingAmount = BankerRounding.round(bookingRow.getInvoiceAmount() + bookingAmount);
            bookingHotelAmount = BankerRounding.round(bookingRow.getHotelInvoiceAmount() + bookingHotelAmount);
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
        bookingDto.setRateChild(BankerRounding.round(total));
    }

    public void calculateRateAdults(ManageBookingDto bookingDto) {
        double total = bookingDto.getRoomRates().stream()
                .mapToDouble(rate -> Optional.ofNullable(rate.getRateAdult())
                .orElse(0.0))
                .sum();
        bookingDto.setRateAdult(BankerRounding.round(total));
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
        Long denominate = nights == 0 ? adults : (nights * adults);
        return BankerRounding.round(rateAmount / denominate);
    }

    private Double calculateRateChild(Double rateAmount, Long nights, Integer children) {
        if(children == 0 && nights == 0) {
            return rateAmount;
        }
        else if(children == 0)
        {
            return BankerRounding.round(rateAmount / nights);
        }
        else if(nights == 0)
        {
            return BankerRounding.round(rateAmount / children);
        }

        return BankerRounding.round(rateAmount / (nights * children));
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
        manageRoomRateDto.setHotelAmount(BankerRounding.round(bookingDto.getHotelAmount()));
        manageRoomRateDto.setNights(bookingDto.getNights());
        manageRoomRateDto.setRoomNumber(bookingDto.getRoomNumber());
        manageRoomRateDto.setInvoiceAmount(BankerRounding.round(bookingDto.getInvoiceAmount()));
        if (bookingDto.getAdults() > 0) {
            manageRoomRateDto.setRateAdult(this.calculateRateAdult(manageRoomRateDto.getInvoiceAmount(),
                    bookingDto.getNights(), bookingDto.getAdults()));
            manageRoomRateDto.setRateChild(0.00);
        }
        else {
            manageRoomRateDto.setRateAdult(0.00);
            manageRoomRateDto.setRateChild(this.calculateRateChild(manageRoomRateDto.getInvoiceAmount(), bookingDto.getNights(), bookingDto.getChildren()));
        }
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

    private BookingImportCache createCacheInsist(BookingRow bookingRow, ManageAgencyDto agencyDto) {
        BookingImportCache bookingImportCache = new BookingImportCache(bookingRow);
        bookingImportCache.setInsistImportProcessId(bookingRow.getInsistImportProcessId());
        bookingImportCache.setImportProcessId(bookingRow.getImportProcessId());
        bookingImportCache.setInsistImportProcessBookingId(bookingRow.getInsistImportProcessBookingId());
        bookingImportCache.setGenerationType(agencyDto.getGenerationType().name());
        repository.save(bookingImportCache);
        return bookingImportCache;
    }

    private void createInvoiceHistory(ManageInvoiceDto manageInvoice, String employee) {
        this.invoiceStatusHistoryService.create(
                new InvoiceStatusHistoryDto(
                        UUID.randomUUID(),
                        manageInvoice,
                        "The invoice data was inserted.",
                        LocalDateTime.now(),
                        this.employeeService.getEmployeeFullName(employee),
                        EInvoiceStatus.PROCESSED,
                        0L
                )
        );
    }

    private void cantAdultsValid(List<BookingRow> rowList){
        int cont = rowList.stream().map(BookingRow::getAdults).reduce(0.0, Double::sum).intValue();
        if (cont <= 0){
            throw new BusinessException(
                    DomainErrorMessage.CANT_ADULTS_NOT_VALID,
                    DomainErrorMessage.CANT_ADULTS_NOT_VALID.getReasonPhrase() + " Hotel Invoice Number: " + rowList.get(0).getHotelInvoiceNumber() + "."
            );
        }
    }
}
