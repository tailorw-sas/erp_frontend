package com.kynsoft.finamer.invoicing.infrastructure.services.processing;

import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dto.roomrate.UnifiedRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.*;
import com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.config.RoomRateProcessingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Processor that converts validated Room Rates into Bookings and Invoices following
 * business rules. Handles batch processing with controlled transactions.
 * Flow: Room Rates → Group by Business Rules → Create Bookings → Group by Invoice Rules → Create Invoices
 */
@Service
@Slf4j
public class RoomRateProcessor {

    private final IManageHotelService hotelService;
    private final IManageAgencyService agencyService;
    private final IManageRoomTypeService roomTypeService;
    private final IManageRatePlanService ratePlanService;
    private final IManageNightTypeService nightTypeService;
    private final IManageInvoiceService invoiceService;
//    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;
//    private final IManageEmployeeService employeeService;

    private final RoomRateProcessingConfig.BatchSizeConfig batchConfig;

    private final TaskExecutor batchExecutor;

    public RoomRateProcessor(
            IManageHotelService hotelService,
            IManageAgencyService agencyService,
            IManageRoomTypeService roomTypeService,
            IManageRatePlanService ratePlanService,
            IManageNightTypeService nightTypeService,
            IManageInvoiceService invoiceService,
            IManageInvoiceStatusService invoiceStatusService,
            IManageInvoiceTypeService invoiceTypeService,
            RoomRateProcessingConfig.BatchSizeConfig batchConfig,
            @Qualifier("batchProcessor") TaskExecutor batchExecutor
    ) {
        this.hotelService = hotelService;
        this.agencyService = agencyService;
        this.roomTypeService = roomTypeService;
        this.ratePlanService = ratePlanService;
        this.nightTypeService = nightTypeService;
        this.invoiceService = invoiceService;
        this.batchConfig = batchConfig;
        this.batchExecutor = batchExecutor;
    }

    /**
     * Processes valid room rates to create bookings and invoices.
     *
     * @param validRoomRates Room rates that passed all validations
     * @param importType Type of import operation (NONE, INVOICE_BOOKING_FROM_FILE, BOOKING_FROM_FILE_VIRTUAL_HOTEL, INSIST)
     * @param invoiceStatus Status to assign to generated invoices
     * @param invoiceType Type to assign to generated invoices
     * @param importProcessId Import process ID
     * @return Processing result with statistics
     */
    public Mono<ProcessingResult> processRoomRates(List<UnifiedRoomRateDto> validRoomRates, ImportType importType,
                                                   ManageInvoiceStatusDto invoiceStatus, ManageInvoiceTypeDto invoiceType, String importProcessId) {
        log.info("Starting processing of {} room rates for process: {}", validRoomRates.size(), importProcessId);

        return Mono.fromCallable(() -> {

                    // 1. Group room rates according to business rules to create bookings
                    Map<String, List<UnifiedRoomRateDto>> bookingGroups = groupRoomRatesForBookings(validRoomRates);
                    log.debug("Grouped {} room rates into {} booking groups", validRoomRates.size(), bookingGroups.size());

                    // 2. Process in batches to handle high volume
                    return processInBatches(bookingGroups, importType, invoiceStatus, invoiceType, importProcessId);

                })
                .subscribeOn(Schedulers.fromExecutor(batchExecutor))
                .doOnError(error -> log.error("Error processing room rates for process: {}", importProcessId, error));
    }

    /**
     * Processes grouped room rates in batches to optimize performance and transaction handling.
     *
     * @param bookingGroups Map where each key represents a booking group key (e.g., "HOTEL|AGENCY|DATE|..."),
     *                      and the value is a list of room rates that belong to that group.
     * @param importType Type of import operation (NONE, INVOICE_BOOKING_FROM_FILE, BOOKING_FROM_FILE_VIRTUAL_HOTEL, INSIST)
     * @param invoiceStatus Status to assign to generated invoices
     * @param invoiceType Type to assign to generated invoices
     * @param importProcessId Identifier of the current import process used for tracking and logging
     * @return ProcessingResult containing all generated bookings and invoices from all batches
     */
    private ProcessingResult processInBatches(Map<String, List<UnifiedRoomRateDto>> bookingGroups, ImportType importType,
                                              ManageInvoiceStatusDto invoiceStatus, ManageInvoiceTypeDto invoiceType, String importProcessId) {

        List<Map.Entry<String, List<UnifiedRoomRateDto>>> groupList = new ArrayList<>(bookingGroups.entrySet());
        int batchSize = batchConfig.getAdjustedBatchSize(batchConfig.getBookingCreationBatchSize(), groupList.size());

        List<ManageInvoiceDto> allInvoices = new ArrayList<>();
        List<ManageBookingDto> allBookings = new ArrayList<>();

        // Process in batches
        return Flux.fromIterable(groupList)
                .buffer(batchSize)
                .flatMap(batch -> processBatch(batch, importType, invoiceStatus, invoiceType, importProcessId), batchConfig.getMaxConcurrentBatches())
                .collectList()
                .map(batchResults -> {
                    // Consolidate results from all batches
                    batchResults.forEach(result -> {
                        allInvoices.addAll(result.getInvoices());
                        allBookings.addAll(result.getInvoices().stream().flatMap(i -> i.getBookings().stream()).collect(Collectors.toList()));
                    });

                    return ProcessingResult.builder()
                            .roomRatesProcessed(bookingGroups.values().stream().mapToInt(List::size).sum())
                            .bookingsCreated(allBookings.size())
                            .invoicesCreated(allInvoices.size())
                            .invoices(allInvoices)
                            .bookings(allBookings)
                            .build();
                })
                .block(); // Block because we are in a Mono.fromCallable
    }

    /**
     * Processes a batch of grouped room rates to generate bookings and invoices.
     *
     * @param batch A list of grouped room rates, where each entry contains the group key and the associated list of room rates.
     * @param importType Type of import operation (NONE, INVOICE_BOOKING_FROM_FILE, BOOKING_FROM_FILE_VIRTUAL_HOTEL, INSIST)
     * @param invoiceStatus Status to assign to generated invoices
     * @param invoiceType Type to assign to generated invoices
     * @param importProcessId Identifier of the import process, used for tracking and traceability
     * @return Mono containing the result of the processed batch, including generated invoices
     */
    @Transactional
    public Mono<BatchResult> processBatch(List<Map.Entry<String, List<UnifiedRoomRateDto>>> batch, ImportType importType,
                                          ManageInvoiceStatusDto invoiceStatus, ManageInvoiceTypeDto invoiceType, String importProcessId) {
        return Mono.fromCallable(() -> {
            log.debug("Processing batch of {} booking groups", batch.size());

            // 1. Create bookings and group them by invoice UUID
            Map<UUID, List<ManageBookingDto>> invoiceGroups = batch.stream()
                .flatMap(entry -> {
                    List<UnifiedRoomRateDto> roomRates = entry.getValue();
                    UUID invoiceGroupId = UUID.randomUUID();

                    // If generation type is not ByBooking, regroup
                    Map<String, List<UnifiedRoomRateDto>> regrouped = (!roomRates.isEmpty() &&
                        roomRates.get(0).getGenerationType() != EGenerationType.ByBooking)
                        ? roomRates.stream().collect(Collectors.groupingBy(UnifiedRoomRateDto::forceGroupByBooking))
                        : Collections.singletonMap(entry.getKey(), roomRates);

                    return regrouped.values().stream()
                        .map(rates -> {
                            try {
                                ManageBookingDto booking = createBookingFromRoomRates(rates);
                                return new AbstractMap.SimpleEntry<>(invoiceGroupId, booking);
                            } catch (Exception e) {
                                log.error("Error creating booking: {}", e.getMessage());
                                return null;
                            }
                        })
                        .filter(Objects::nonNull);
                })
                .collect(Collectors.groupingBy(
                    Map.Entry::getKey,
                    Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

            // 2. Use invoiceGroups directly for invoice creation
            List<ManageInvoiceDto> batchInvoices = new ArrayList<>();
            for (Map.Entry<UUID, List<ManageBookingDto>> entry : invoiceGroups.entrySet()) {
                UUID invoiceKey = entry.getKey();
                List<ManageBookingDto> bookings = entry.getValue();

                try {
                    ManageInvoiceDto invoice = createInvoiceFromBookings(invoiceKey, bookings, importType, invoiceStatus, invoiceType, importProcessId);
                    batchInvoices.add(invoice);
                } catch (Exception e) {
                    log.error("Error creating invoice for group {}: {}", invoiceKey, e.getMessage());
                }
            }

            return BatchResult.builder()
                    .invoices(batchInvoices)
                    .build();
        })
        .subscribeOn(Schedulers.fromExecutor(batchExecutor))
        .retry(3); // Retry in case of concurrency conflicts
    }

    /**
     * Groups the provided room rates based on a business rule into a map, where each group corresponds to a set of room rates
     * that should be treated as a single booking group.
     *
     * @param roomRates List of validated room rates to be grouped
     * @return A map where the key is a group identifier string and the value is the list of room rates belonging to that group
     */
    private Map<String, List<UnifiedRoomRateDto>> groupRoomRatesForBookings(List<UnifiedRoomRateDto> roomRates) {
        return roomRates.stream()
                .collect(Collectors.groupingBy(roomRate -> {
                    // Group by: Hotel + Agency + Transaction Date + (Logic of business)
                    return roomRate.calculateBookingGroupKey();
                }));
    }

    /**
     * Creates a booking DTO from a list of room rate DTOs.
     *
     * @param roomRates List of room rate DTOs that should be combined into one booking
     * @return A ManageBookingDto object with all information aggregated from the room rates
     */
    private ManageBookingDto createBookingFromRoomRates(List<UnifiedRoomRateDto> roomRates) {
        // Use the first room rate as a basis for common data
        UnifiedRoomRateDto firstRate = roomRates.get(0);

        // Load required master data
        ManageHotelDto hotel = hotelService.findByCode(firstRate.getHotelCode());
        //ManageAgencyDto agency = agencyService.findByCode(firstRate.getAgencyCode());
        ManageRoomTypeDto roomType = loadRoomTypeIfPresent(firstRate.getRoomType(), hotel.getCode());
        ManageRatePlanDto ratePlan = loadRatePlanIfPresent(firstRate.getRatePlan(), hotel.getCode());
        ManageNightTypeDto nightType = loadNightTypeIfPresent(firstRate.getNightType());

        // Create the booking
        ManageBookingDto booking = new ManageBookingDto();
        booking.setId(UUID.randomUUID());

        // Basic booking data (from the first room rate)
        booking.setCheckIn(DateUtil.parseDateToDateTime(firstRate.getCheckIn()));
        booking.setCheckOut(DateUtil.parseDateToDateTime(firstRate.getCheckOut()));
        booking.setFirstName(firstRate.getFirstName() != null ? firstRate.getFirstName() : "");
        booking.setLastName(firstRate.getLastName() != null ? firstRate.getLastName() : "");
        booking.setFullName(firstRate.getFullName());
        booking.setHotelBookingNumber(firstRate.getHotelBookingNumber() != null ? firstRate.getHotelBookingNumber() : "");
        booking.setCouponNumber(firstRate.getCoupon() != null ? firstRate.getCoupon() : "");
        booking.setDescription(firstRate.getRemarks() != null ? firstRate.getRemarks().trim() : "");
        booking.setRoomNumber(firstRate.getRoomNumber());
        booking.setBookingDate(firstRate.getBookingDate() != null ?
                DateUtil.parseDateToDateTime(firstRate.getBookingDate()) : LocalDateTime.of(1999, 1, 1, 0, 0));
        booking.setHotelCreationDate(DateUtil.parseDateToDateTime(firstRate.getTransactionDate()));

        // Master data
        booking.setRoomType(roomType);
        booking.setRatePlan(ratePlan);
        booking.setNightType(nightType);

        // Create room rates for this booking
        List<ManageRoomRateDto> roomRatesList = new ArrayList<>();
        double totalBookingAmount = 0;
        double totalHotelAmount = 0;

        for (UnifiedRoomRateDto roomRateData : roomRates) {
            ManageRoomRateDto roomRate = createRoomRateDto(roomRateData);
            roomRatesList.add(roomRate);
            totalBookingAmount += roomRateData.getInvoiceAmount();
            totalHotelAmount += (roomRateData.getHotelInvoiceAmount() != null ? roomRateData.getHotelInvoiceAmount() : 0);
        }

        booking.setRoomRates(roomRatesList);
        booking.setInvoiceAmount(BankerRounding.round(totalBookingAmount));
        booking.setDueAmount(BankerRounding.round(totalBookingAmount));
        booking.setHotelAmount(BankerRounding.round(totalHotelAmount));

        // Calculate aggregate fields
        calculateBookingAggregates(booking);

        return booking;
    }

    /**
     * Create a ManageRoomRateDto from UnifiedRoomRateDto
     * @param roomRateData
     * @return
     */
    private ManageRoomRateDto createRoomRateDto(UnifiedRoomRateDto roomRateData) {
        ManageRoomRateDto roomRate = new ManageRoomRateDto();
        roomRate.setId(UUID.randomUUID());
        roomRate.setAdults(roomRateData.getAdults() != null ? roomRateData.getAdults().intValue() : 0);
        roomRate.setChildren(roomRateData.getChildren() != null ? roomRateData.getChildren().intValue() : 0);
        roomRate.setCheckIn(DateUtil.parseDateToDateTime(roomRateData.getCheckIn()));
        roomRate.setCheckOut(DateUtil.parseDateToDateTime(roomRateData.getCheckOut()));
        roomRate.setNights(roomRateData.getNights() != null ? roomRateData.getNights().longValue() : 0);
        roomRate.setRoomNumber(roomRateData.getRoomNumber());
        roomRate.setInvoiceAmount(BankerRounding.round(roomRateData.getInvoiceAmount()));
        roomRate.setHotelAmount(roomRateData.getHotelInvoiceAmount() != null ?
                BankerRounding.round(roomRateData.getHotelInvoiceAmount()) : 0.0);

        // Calculate rates per adult and child
        if (roomRate.getAdults() > 0) {
            roomRate.setRateAdult(calculateRateAdult(roomRate.getInvoiceAmount(), roomRate.getNights(), roomRate.getAdults()));
            roomRate.setRateChild(0.0);
        } else {
            roomRate.setRateAdult(0.0);
            roomRate.setRateChild(calculateRateChild(roomRate.getInvoiceAmount(), roomRate.getNights(), roomRate.getChildren()));
        }

        return roomRate;
    }

    /**
     * Calculate aggregated booking fields based on your room rates
     * @param booking
     */
    private void calculateBookingAggregates(ManageBookingDto booking) {
        List<ManageRoomRateDto> rates = booking.getRoomRates();

        // Check-in y check-out (min y max)
        LocalDateTime checkIn = rates.stream()
                .map(ManageRoomRateDto::getCheckIn)
                .min(LocalDateTime::compareTo)
                .orElse(booking.getCheckIn());

        LocalDateTime checkOut = rates.stream()
                .map(ManageRoomRateDto::getCheckOut)
                .max(LocalDateTime::compareTo)
                .orElse(booking.getCheckOut());

        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);

        // Adults and children (maximum)
        int maxAdults = rates.stream()
                .mapToInt(ManageRoomRateDto::getAdults)
                .max()
                .orElse(0);

        int maxChildren = rates.stream()
                .mapToInt(ManageRoomRateDto::getChildren)
                .max()
                .orElse(0);

        booking.setAdults(maxAdults);
        booking.setChildren(maxChildren);

        // Nights (sum of all nights)
        long totalNights = rates.stream()
                .mapToLong(ManageRoomRateDto::getNights)
                .sum();

        booking.setNights(totalNights);

        // Rate Adult and Rate Child (sum)
        double totalRateAdult = rates.stream()
                .mapToDouble(rate -> Optional.ofNullable(rate.getRateAdult()).orElse(0.0))
                .sum();

        double totalRateChild = rates.stream()
                .mapToDouble(rate -> Optional.ofNullable(rate.getRateChild()).orElse(0.0))
                .sum();

        booking.setRateAdult(BankerRounding.round(totalRateAdult));
        booking.setRateChild(BankerRounding.round(totalRateChild));
    }

    /**
     * Creates an invoice from a list of booking DTOs, using the provided invoice ID and import process identifier.
     *
     * @param invoiceKey UUID representing the identifier of the invoice
     * @param bookings List of bookings to include in the invoice
     * @param importType Type of import operation (NONE, INVOICE_BOOKING_FROM_FILE, BOOKING_FROM_FILE_VIRTUAL_HOTEL, INSIST)
     * @param invoiceStatus Status to assign to the invoice
     * @param invoiceType Type to assign to the invoice
     * @param importProcessId ID of the current import process for tracking purposes
     * @return The generated ManageInvoiceDto with aggregated booking data
     */
    private ManageInvoiceDto createInvoiceFromBookings(UUID invoiceKey, List<ManageBookingDto> bookings, ImportType importType,
                                                       ManageInvoiceStatusDto invoiceStatus, ManageInvoiceTypeDto invoiceType, String importProcessId) {
        // Use the first room rate as a basis for common data
        ManageBookingDto firstBooking = bookings.get(0);

        // Create the invoice
        ManageInvoiceDto invoice = new ManageInvoiceDto();
        invoice.setId(invoiceKey);
        invoice.setInvoiceType(EInvoiceType.INVOICE);
        invoice.setManageInvoiceType(invoiceType);
        invoice.setStatus(EInvoiceStatus.PROCESSED);
        invoice.setManageInvoiceStatus(invoiceStatus);
        invoice.setIsManual(false);
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setBookings(bookings);

        // Calculate total amounts
        double totalAmount = bookings.stream()
                .mapToDouble(ManageBookingDto::getInvoiceAmount)
                .sum();

        invoice.setInvoiceAmount(BankerRounding.round(totalAmount));
        invoice.setDueAmount(BankerRounding.round(totalAmount));
        invoice.setOriginalAmount(BankerRounding.round(totalAmount));

        // Configure import type
        invoice.setImportType(importType);

        // Put the booking invoice number (here the DB trigger would be used)
        invoice.setInvoiceNo(parseLongOrDefault(firstBooking.getHotelInvoiceNumber()));

        // Persist the invoice (the trigger handles concurrency)
        invoiceService.insert(invoice);

        return invoice;
    }

    // === AUXILIARY METHODS ===

    /**
     * Attempts to retrieve the Room Type from the service if the provided code is not null or empty.
     * If the room type cannot be found, logs a warning and returns null.
     *
     * @param roomTypeCode the code of the room type
     * @param hotelCode the code of the hotel associated with the room type
     * @return the corresponding ManageRoomTypeDto if found, otherwise null
     */
    private ManageRoomTypeDto loadRoomTypeIfPresent(String roomTypeCode, String hotelCode) {
        if (roomTypeCode != null && !roomTypeCode.trim().isEmpty()) {
            try {
                return roomTypeService.findManageRoomTypenByCodeAndHotelCode(roomTypeCode, hotelCode);
            } catch (Exception e) {
                log.warn("Room type not found: {} for hotel: {}", roomTypeCode, hotelCode);
            }
        }

        return null;
    }

    /**
     * Attempts to retrieve the Rate Plan from the service if the provided code is not null or empty.
     * If the rate plan cannot be found, logs a warning and returns null.
     *
     * @param ratePlanCode the code of the rate plan
     * @param hotelCode the code of the hotel associated with the rate plan
     * @return the corresponding ManageRatePlanDto if found, otherwise null
     */
    private ManageRatePlanDto loadRatePlanIfPresent(String ratePlanCode, String hotelCode) {
        if (ratePlanCode != null && !ratePlanCode.trim().isEmpty()) {
            try {
                return ratePlanService.findManageRatePlanByCodeAndHotelCode(ratePlanCode, hotelCode);
            } catch (Exception e) {
                log.warn("Rate plan not found: {} for hotel: {}", ratePlanCode, hotelCode);
            }
        }

        return null;
    }

    /**
     * Attempts to retrieve the Night Type from the service if the provided code is not null or empty.
     * If the night type cannot be found, logs a warning and returns null.
     *
     * @param nightTypeCode the code of the night type
     * @return the corresponding ManageNightTypeDto if found, otherwise null
     */
    private ManageNightTypeDto loadNightTypeIfPresent(String nightTypeCode) {
        if (nightTypeCode != null && !nightTypeCode.trim().isEmpty()) {
            try {
                return nightTypeService.findByCode(nightTypeCode);
            } catch (Exception e) {
                log.warn("Night type not found: {}", nightTypeCode);
            }
        }
        return null;
    }

    /**
     * Calculates the per-adult rate amount based on total invoice amount, nights, and number of adults.
     *
     * @param rateAmount the total invoice amount
     * @param nights the number of nights
     * @param adults the number of adults
     * @return the calculated rate per adult
     */
    private Double calculateRateAdult(Double rateAmount, Long nights, Integer adults) {
        if (adults == 0) return 0.0;
        Long denominator = nights == 0 ? adults : (nights * adults);
        return BankerRounding.round(rateAmount / denominator);
    }

    /**
     * Calculates the per-child rate amount based on total invoice amount, nights, and number of children.
     *
     * @param rateAmount the total invoice amount
     * @param nights the number of nights
     * @param children the number of children
     * @return the calculated rate per child
     */
    private Double calculateRateChild(Double rateAmount, Long nights, Integer children) {
        if (children == 0 && nights == 0) return rateAmount;
        if (children == 0) return BankerRounding.round(rateAmount / nights);
        if (nights == 0) return BankerRounding.round(rateAmount / children);
        return BankerRounding.round(rateAmount / (nights * children));
    }

    /**
     * Generates a placeholder invoice number based on the current timestamp.
     * Note: This is a fallback. The actual invoice number is generated by a DB trigger.
     *
     * @param invoiceType the type of the invoice
     * @return a generated invoice number string
     */
    private String generateInvoiceNumber(ManageInvoiceTypeDto invoiceType) {
        // Placeholder - this is actually handled by the BD trigger
        return "INV-" + System.currentTimeMillis();
    }

    // === CLASES DE RESULTADO ===

    @lombok.Data
    @lombok.Builder
    public static class ProcessingResult {
        private int roomRatesProcessed;
        private int bookingsCreated;
        private int invoicesCreated;
        private List<ManageInvoiceDto> invoices;
        private List<ManageBookingDto> bookings;
    }

    @lombok.Data
    @lombok.Builder
    public static class BatchResult {
        private List<ManageInvoiceDto> invoices;
    }

    public static long parseLongOrDefault(String input) {
        if (input != null && input.matches("\\d+")) {
            return Long.parseLong(input);
        }
        return 0;
    }
}