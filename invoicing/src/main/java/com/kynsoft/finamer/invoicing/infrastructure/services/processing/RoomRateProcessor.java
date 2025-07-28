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
 * Procesador que convierte Room Rates validados en Bookings e Invoices siguiendo
 * las reglas de negocio. Maneja procesamiento por lotes con transacciones controladas.
 * Flujo: Room Rates → Group by Business Rules → Create Bookings → Group by Invoice Rules → Create Invoices
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoomRateProcessor {

    private final IManageHotelService hotelService;
    private final IManageAgencyService agencyService;
    private final IManageRoomTypeService roomTypeService;
    private final IManageRatePlanService ratePlanService;
    private final IManageNightTypeService nightTypeService;
    private final IManageInvoiceService invoiceService;
    private final IManageInvoiceStatusService invoiceStatusService;
    private final IManageInvoiceTypeService invoiceTypeService;
    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;
    private final IManageEmployeeService employeeService;

    private final RoomRateProcessingConfig.BatchSizeConfig batchConfig;

    @Qualifier("batchProcessor")
    private final TaskExecutor batchExecutor;

    /**
     * Procesa room rates válidos para crear bookings e invoices
     *
     * @param validRoomRates Room rates que pasaron todas las validaciones
     * @param importProcessId ID del proceso de importación
     * @return Resultado del procesamiento con estadísticas
     */
    public Mono<ProcessingResult> processRoomRates(List<UnifiedRoomRateDto> validRoomRates, String importProcessId) {
        log.info("Starting processing of {} room rates for process: {}", validRoomRates.size(), importProcessId);

        return Mono.fromCallable(() -> {

                    // 1. Agrupar room rates según reglas de negocio para formar bookings
                    Map<String, List<UnifiedRoomRateDto>> bookingGroups = groupRoomRatesForBookings(validRoomRates);
                    log.debug("Grouped {} room rates into {} booking groups", validRoomRates.size(), bookingGroups.size());

                    // 2. Procesar en lotes para manejar volumen alto
                    return processInBatches(bookingGroups, importProcessId);

                })
                .subscribeOn(Schedulers.fromExecutor(batchExecutor))
                .doOnError(error -> log.error("Error processing room rates for process: {}", importProcessId, error));
    }

    /**
     * Procesa los grupos en lotes para optimizar performance y manejo de transacciones
     */
    private ProcessingResult processInBatches(Map<String, List<UnifiedRoomRateDto>> bookingGroups, String importProcessId) {

        List<Map.Entry<String, List<UnifiedRoomRateDto>>> groupList = new ArrayList<>(bookingGroups.entrySet());
        int batchSize = batchConfig.getAdjustedBatchSize(batchConfig.getBookingCreationBatchSize(), groupList.size());

        List<ManageInvoiceDto> allInvoices = new ArrayList<>();
        List<ManageBookingDto> allBookings = new ArrayList<>();

        // Procesar en lotes
        return Flux.fromIterable(groupList)
                .buffer(batchSize)
                .flatMap(batch -> processBatch(batch, importProcessId), batchConfig.getMaxConcurrentBatches())
                .collectList()
                .map(batchResults -> {
                    // Consolidar resultados de todos los lotes
                    batchResults.forEach(result -> {
                        allInvoices.addAll(result.getInvoices());
                        allBookings.addAll(result.getBookings());
                    });

                    return ProcessingResult.builder()
                            .roomRatesProcessed(bookingGroups.values().stream().mapToInt(List::size).sum())
                            .bookingsCreated(allBookings.size())
                            .invoicesCreated(allInvoices.size())
                            .invoices(allInvoices)
                            .bookings(allBookings)
                            .build();
                })
                .block(); // Block porque estamos en un Mono.fromCallable
    }

    /**
     * Procesa un lote de grupos de booking
     */
    @Transactional
    public Mono<BatchResult> processBatch(List<Map.Entry<String, List<UnifiedRoomRateDto>>> batch, String importProcessId) {
        return Mono.fromCallable(() -> {
                    log.debug("Processing batch of {} booking groups", batch.size());

                    List<ManageBookingDto> batchBookings = new ArrayList<>();

                    // 1. Crear bookings para cada grupo en el lote
                    for (Map.Entry<String, List<UnifiedRoomRateDto>> entry : batch) {
                        String bookingKey = entry.getKey();
                        List<UnifiedRoomRateDto> roomRates = entry.getValue();

                        try {
                            ManageBookingDto booking = createBookingFromRoomRates(bookingKey, roomRates);
                            batchBookings.add(booking);
                        } catch (Exception e) {
                            log.error("Error creating booking for group {}: {}", bookingKey, e.getMessage());
                            // En un entorno real, podrías decidir si continuar o fallar todo el lote
                        }
                    }

                    // 2. Agrupar bookings para invoices
                    Map<String, List<ManageBookingDto>> invoiceGroups = groupBookingsForInvoices(batchBookings);

                    // 3. Crear invoices
                    List<ManageInvoiceDto> batchInvoices = new ArrayList<>();
                    for (Map.Entry<String, List<ManageBookingDto>> entry : invoiceGroups.entrySet()) {
                        String invoiceKey = entry.getKey();
                        List<ManageBookingDto> bookings = entry.getValue();

                        try {
                            ManageInvoiceDto invoice = createInvoiceFromBookings(invoiceKey, bookings, importProcessId);
                            batchInvoices.add(invoice);
                        } catch (Exception e) {
                            log.error("Error creating invoice for group {}: {}", invoiceKey, e.getMessage());
                        }
                    }

                    return BatchResult.builder()
                            .bookings(batchBookings)
                            .invoices(batchInvoices)
                            .build();
                })
                .subscribeOn(Schedulers.fromExecutor(batchExecutor))
                .retry(3); // Retry en caso de conflictos de concurrencia
    }

    /**
     * Agrupa room rates según las reglas de negocio para formar bookings
     */
    private Map<String, List<UnifiedRoomRateDto>> groupRoomRatesForBookings(List<UnifiedRoomRateDto> roomRates) {
        return roomRates.stream()
                .collect(Collectors.groupingBy(roomRate -> {
                    // Agrupar por: Hotel + Agency + Hotel Booking Number + Transaction Date
                    return String.format("%s|%s|%s|%s",
                            roomRate.getHotelCode(),
                            roomRate.getAgencyCode(),
                            roomRate.getHotelBookingNumber(),
                            roomRate.getTransactionDate());
                }));
    }

    /**
     * Crea un booking a partir de un grupo de room rates
     */
    private ManageBookingDto createBookingFromRoomRates(String bookingKey, List<UnifiedRoomRateDto> roomRates) {
        // Usar el primer room rate como base para datos comunes
        UnifiedRoomRateDto firstRate = roomRates.get(0);

        // Cargar datos maestros necesarios
        ManageHotelDto hotel = hotelService.findByCode(firstRate.getHotelCode());
        ManageAgencyDto agency = agencyService.findByCode(firstRate.getAgencyCode());
        ManageRoomTypeDto roomType = loadRoomTypeIfPresent(firstRate.getRoomType(), hotel.getCode());
        ManageRatePlanDto ratePlan = loadRatePlanIfPresent(firstRate.getRatePlan(), hotel.getCode());
        ManageNightTypeDto nightType = loadNightTypeIfPresent(firstRate.getNightType());

        // Crear el booking
        ManageBookingDto booking = new ManageBookingDto();
        booking.setId(UUID.randomUUID());

        // Datos básicos del booking (del primer room rate)
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

        // Datos maestros
        booking.setRoomType(roomType);
        booking.setRatePlan(ratePlan);
        booking.setNightType(nightType);

        // Crear room rates para este booking
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

        // Calcular campos agregados
        calculateBookingAggregates(booking);

        return booking;
    }

    /**
     * Crea un ManageRoomRateDto a partir de UnifiedRoomRateDto
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

        // Calcular rates por adulto y niño
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
     * Calcula campos agregados del booking basados en sus room rates
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

        // Adults y children (máximo)
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

        // Nights (suma de todas las noches)
        long totalNights = rates.stream()
                .mapToLong(ManageRoomRateDto::getNights)
                .sum();

        booking.setNights(totalNights);

        // Rate Adult y Rate Child (suma)
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
     * Agrupa bookings para formar invoices
     */
    private Map<String, List<ManageBookingDto>> groupBookingsForInvoices(List<ManageBookingDto> bookings) {
        return bookings.stream()
                .collect(Collectors.groupingBy(booking -> {
                    // Agrupar por: Hotel + Agency + Transaction Date
                    // Aquí necesitarías acceder al hotel y agency del booking
                    return String.format("%s|%s|%s",
                            "HOTEL_CODE", // Necesitas obtener esto del booking
                            "AGENCY_CODE", // Necesitas obtener esto del booking
                            booking.getHotelCreationDate().toLocalDate().toString());
                }));
    }

    /**
     * Crea una invoice a partir de un grupo de bookings
     */
    private ManageInvoiceDto createInvoiceFromBookings(String invoiceKey, List<ManageBookingDto> bookings, String importProcessId) {
        // Usar el primer booking para datos comunes
        ManageBookingDto firstBooking = bookings.get(0);

        // Obtener datos maestros para la invoice
        ManageInvoiceStatusDto invoiceStatus = invoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.PROCESSED);
        ManageInvoiceTypeDto invoiceType = invoiceTypeService.findByEInvoiceType(EInvoiceType.INVOICE);

        // Crear la invoice
        ManageInvoiceDto invoice = new ManageInvoiceDto();
        invoice.setId(UUID.randomUUID());
        invoice.setInvoiceType(EInvoiceType.INVOICE);
        invoice.setManageInvoiceType(invoiceType);
        invoice.setStatus(EInvoiceStatus.PROCESSED);
        invoice.setManageInvoiceStatus(invoiceStatus);
        invoice.setIsManual(false);
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setBookings(bookings);

        // Calcular montos totales
        double totalAmount = bookings.stream()
                .mapToDouble(ManageBookingDto::getInvoiceAmount)
                .sum();

        invoice.setInvoiceAmount(BankerRounding.round(totalAmount));
        invoice.setDueAmount(BankerRounding.round(totalAmount));
        invoice.setOriginalAmount(BankerRounding.round(totalAmount));

        // Configurar import type
        invoice.setImportType(ImportType.INVOICE_BOOKING_FROM_FILE);

        // Generar número de invoice (aquí se usaría el trigger de BD)
        invoice.setInvoiceNumber(generateInvoiceNumber(invoiceType));

        // Persistir la invoice (el trigger maneja concurrencia)
        invoice = invoiceService.create(invoice);

        return invoice;
    }

    // === MÉTODOS AUXILIARES ===

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

    private Double calculateRateAdult(Double rateAmount, Long nights, Integer adults) {
        if (adults == 0) return 0.0;
        Long denominator = nights == 0 ? adults : (nights * adults);
        return BankerRounding.round(rateAmount / denominator);
    }

    private Double calculateRateChild(Double rateAmount, Long nights, Integer children) {
        if (children == 0 && nights == 0) return rateAmount;
        if (children == 0) return BankerRounding.round(rateAmount / nights);
        if (nights == 0) return BankerRounding.round(rateAmount / children);
        return BankerRounding.round(rateAmount / (nights * children));
    }

    private String generateInvoiceNumber(ManageInvoiceTypeDto invoiceType) {
        // Placeholder - en realidad esto lo maneja el trigger de BD
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
        private List<ManageBookingDto> bookings;
        private List<ManageInvoiceDto> invoices;
    }
}