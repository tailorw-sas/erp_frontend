package com.kynsoft.finamer.invoicing.infrastructure.services.validation;

import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageClientDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.cache.ReferenceDataCache;
import com.kynsoft.finamer.invoicing.domain.dto.roomrate.UnifiedRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.dto.validation.ValidationError;
import com.kynsoft.finamer.invoicing.domain.dto.validation.ValidationResult;
import com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Validador de reglas de negocio que maneja dependencias complejas entre room rates.
 * Valida en memoria con datos precargados y aplica reglas cross-rate como:
 * - Al menos un adulto por booking
 * - Consistencia en fechas y montos
 * - Validaciones de datos maestros
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessRuleValidator {

    @Qualifier("validationExecutor")
    private final TaskExecutor validationExecutor;

    /**
     * Ejecuta validación completa con reglas de negocio complejas
     *
     * @param roomRates Lista de room rates a validar
     * @param cache Cache con datos de referencia precargados
     * @return Resultado con room rates válidos y todos los errores encontrados
     */
    public Mono<ValidationResult> validateWithBusinessRules(List<UnifiedRoomRateDto> roomRates,
                                                            ReferenceDataCache cache) {
        log.info("Starting business rule validation for {} room rates", roomRates.size());
        long startTime = System.currentTimeMillis();

        return Mono.fromCallable(() -> {

                    // Usar estructuras thread-safe para paralelización
                    List<ValidationError> allErrors = new CopyOnWriteArrayList<>();
                    List<UnifiedRoomRateDto> validRoomRates = new CopyOnWriteArrayList<>();

                    // 1. Validaciones individuales primero (pueden ejecutarse en paralelo)
                    Map<String, List<ValidationError>> individualErrors = validateIndividualRoomRates(roomRates, cache);

                    // 2. Si hay errores individuales críticos, no continuar con validaciones de grupo
                    List<ValidationError> criticalErrors = individualErrors.values().stream()
                            .flatMap(List::stream)
                            .filter(ValidationError::isBlocking)
                            .collect(Collectors.toList());

                    if (!criticalErrors.isEmpty()) {
                        allErrors.addAll(criticalErrors);
                        long validationTime = System.currentTimeMillis() - startTime;
                        log.warn("Validation stopped due to {} critical individual errors", criticalErrors.size());
                        return ValidationResult.withErrors(allErrors, roomRates.size(), validationTime);
                    }

                    // 3. Agregar warnings de validaciones individuales
                    List<ValidationError> warnings = individualErrors.values().stream()
                            .flatMap(List::stream)
                            .filter(error -> !error.isBlocking())
                            .collect(Collectors.toList());
                    allErrors.addAll(warnings);

                    // 4. Agrupar room rates según lógicas de negocio para validaciones cross-rate
                    Map<String, List<UnifiedRoomRateDto>> bookingGroups = groupRoomRatesForBookingValidation(roomRates);
                    log.debug("Grouped {} room rates into {} booking groups", roomRates.size(), bookingGroups.size());

                    // 5. Validar reglas de negocio por grupo (pueden ejecutarse en paralelo)
                    List<ValidationError> businessRuleErrors = validateBusinessRulesByGroup(bookingGroups, cache);
                    allErrors.addAll(businessRuleErrors);

                    // 6. Si no hay errores bloqueantes, todos los room rates son válidos para procesamiento
                    boolean hasBlockingErrors = allErrors.stream().anyMatch(ValidationError::isBlocking);
                    if (!hasBlockingErrors) {
                        validRoomRates.addAll(roomRates);
                    }

                    long validationTime = System.currentTimeMillis() - startTime;
                    log.info("Business rule validation completed in {}ms. Valid: {}, Errors: {}",
                            validationTime, validRoomRates.size(), allErrors.size());

                    return ValidationResult.builder()
                            .validRoomRates(validRoomRates)
                            .errors(allErrors)
                            .totalProcessed(roomRates.size())
                            .validationTimeMs(validationTime)
                            .build();

                })
                .subscribeOn(Schedulers.fromExecutor(validationExecutor))
                .doOnError(error -> log.error("Error during business rule validation", error));
    }

    /**
     * Valida cada room rate individualmente contra datos maestros
     * Incluye TODAS las validaciones del sistema actual optimizadas
     */
    private Map<String, List<ValidationError>> validateIndividualRoomRates(List<UnifiedRoomRateDto> roomRates,
                                                                           ReferenceDataCache cache) {
        Map<String, List<ValidationError>> errorsByRoomRate = new ConcurrentHashMap<>();

        // Procesamiento paralelo de validaciones individuales
        roomRates.parallelStream().forEach(roomRate -> {
            List<ValidationError> roomRateErrors = new ArrayList<>();
            String roomRateKey = roomRate.getUniqueIdentifier();

            // === VALIDACIONES BÁSICAS ===
            validateRequiredFields(roomRate, roomRateErrors);
            validateDataFormats(roomRate, roomRateErrors);

            // === VALIDACIONES DE DATOS MAESTROS ===
            validateHotel(roomRate, cache, roomRateErrors);
            validateAgency(roomRate, cache, roomRateErrors);
            validateRoomType(roomRate, cache, roomRateErrors);
            validateRatePlan(roomRate, cache, roomRateErrors);
            validateNightType(roomRate, cache, roomRateErrors);

            // === VALIDACIONES DE FECHAS ===
            validateCheckInOut(roomRate, roomRateErrors);
            validateBookingDate(roomRate, roomRateErrors);
            validateTransactionDate(roomRate, cache, roomRateErrors);

            // === VALIDACIONES DE MONTOS ===
            validateAmounts(roomRate, roomRateErrors);
            validateHotelInvoiceAmount(roomRate, cache, roomRateErrors);

            // === VALIDACIONES ESPECÍFICAS ===
            validateNames(roomRate, roomRateErrors);
            validateAmountPAX(roomRate, roomRateErrors);
            validateHotelBookingNumber(roomRate, roomRateErrors);
            validateImportType(roomRate, cache, roomRateErrors);
            validateDuplicates(roomRate, cache, roomRateErrors);

            if (!roomRateErrors.isEmpty()) {
                errorsByRoomRate.put(roomRateKey, roomRateErrors);
            }
        });

        return errorsByRoomRate;
    }

    /**
     * Agrupa room rates según las reglas de negocio para formar bookings
     */
    private Map<String, List<UnifiedRoomRateDto>> groupRoomRatesForBookingValidation(List<UnifiedRoomRateDto> roomRates) {
        return roomRates.stream()
                .collect(Collectors.groupingBy(this::calculateBookingGroupKey));
    }

    /**
     * Calcula la clave de agrupación para bookings
     * Esta lógica determina qué room rates forman un booking
     */
    private String calculateBookingGroupKey(UnifiedRoomRateDto roomRate) {
        // Regla de agrupación: Hotel + Agency + Hotel Booking Number + Transaction Date
        return String.format("%s|%s|%s|%s",
                Objects.toString(roomRate.getHotelCode(), ""),
                Objects.toString(roomRate.getAgencyCode(), ""),
                Objects.toString(roomRate.getHotelBookingNumber(), ""),
                Objects.toString(roomRate.getTransactionDate(), ""));
    }

    /**
     * Valida reglas de negocio que aplican a grupos de room rates (bookings)
     */
    private List<ValidationError> validateBusinessRulesByGroup(Map<String, List<UnifiedRoomRateDto>> bookingGroups,
                                                               ReferenceDataCache cache) {
        List<ValidationError> allGroupErrors = new CopyOnWriteArrayList<>();

        // Validar cada grupo en paralelo
        bookingGroups.entrySet().parallelStream().forEach(entry -> {
            String bookingKey = entry.getKey();
            List<UnifiedRoomRateDto> bookingRates = entry.getValue();

            List<ValidationError> groupErrors = new ArrayList<>();

            // REGLA CRÍTICA: Al menos un adulto en el booking completo
            validateAdultsInBooking(bookingKey, bookingRates, groupErrors);

            // REGLA: Consistencia en fechas del booking
            validateDateConsistency(bookingKey, bookingRates, groupErrors);

            // REGLA: Validar montos y cálculos
            validateAmounts(bookingKey, bookingRates, groupErrors);

            // REGLA: Validar duplicados dentro del grupo
            validateDuplicatesInGroup(bookingKey, bookingRates, groupErrors);

            // REGLA: Validar lógica específica según tipo de agencia
            validateAgencySpecificRules(bookingKey, bookingRates, cache, groupErrors);

            allGroupErrors.addAll(groupErrors);
        });

        return allGroupErrors;
    }

    // === VALIDACIONES INDIVIDUALES COMPLETAS ===

    /**
     * HOTEL VALIDATION - Migrado de ImportBookingHotelValidator
     */
    private void validateHotel(UnifiedRoomRateDto roomRate, ReferenceDataCache cache, List<ValidationError> errors) {
        String hotelCode = roomRate.getHotelCode();

        if (isNullOrEmpty(hotelCode)) {
            errors.add(ValidationError.fieldRequired(roomRate.getSourceType(), roomRate.getSourceIdentifier(), "hotelCode"));
            return;
        }

        Optional<ManageHotelDto> hotelOpt = cache.getHotelIfAllowed(hotelCode.toUpperCase().trim());
        if (hotelOpt.isEmpty()) {
            errors.add(ValidationError.businessRuleViolation(
                    roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "hotelCode", "Hotel not found or access denied", null));
            return;
        }

        ManageHotelDto hotel = hotelOpt.get();
        if ("INACTIVE".equals(hotel.getStatus())) {
            errors.add(ValidationError.businessRuleViolation(
                    roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "hotelCode", "Hotel is inactive", null));
        }
    }

    /**
     * AGENCY VALIDATION - Migrado de ImportBookingAgencyValidator
     */
    private void validateAgency(UnifiedRoomRateDto roomRate, ReferenceDataCache cache, List<ValidationError> errors) {
        String agencyCode = roomRate.getAgencyCode();

        if (isNullOrEmpty(agencyCode)) {
            errors.add(ValidationError.fieldRequired(roomRate.getSourceType(), roomRate.getSourceIdentifier(), "agencyCode"));
            return;
        }

        Optional<ManageAgencyDto> agencyOpt = cache.getAgencyIfAllowed(agencyCode.toUpperCase().trim());
        if (agencyOpt.isEmpty()) {
            errors.add(ValidationError.businessRuleViolation(
                    roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "agencyCode", "Agency not found or access denied", null));
            return;
        }

        ManageAgencyDto agency = agencyOpt.get();
        if ("INACTIVE".equals(agency.getStatus())) {
            errors.add(ValidationError.businessRuleViolation(
                    roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "agencyCode", "Agency is inactive", null));
        }
    }

    /**
     * ROOM TYPE VALIDATION - Migrado de ImportBookingRoomTypeValidator
     */
    private void validateRoomType(UnifiedRoomRateDto roomRate, ReferenceDataCache cache, List<ValidationError> errors) {
        String roomTypeCode = roomRate.getRoomType();

        if (!isNullOrEmpty(roomTypeCode)) {
            String upperRoomType = roomTypeCode.toUpperCase().trim();
            if (!cache.hasValidRoomType(upperRoomType, roomRate.getHotelCode())) {
                errors.add(ValidationError.businessRuleViolation(
                        roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "roomType", "Room type not found or doesn't belong to hotel", null));
            }
        }
    }

    /**
     * RATE PLAN VALIDATION - Migrado de ImportBookingRatePlanValidator
     */
    private void validateRatePlan(UnifiedRoomRateDto roomRate, ReferenceDataCache cache, List<ValidationError> errors) {
        String ratePlanCode = roomRate.getRatePlan();

        if (!isNullOrEmpty(ratePlanCode)) {
            String upperRatePlan = ratePlanCode.toUpperCase().trim();
            if (!cache.hasValidRatePlan(upperRatePlan, roomRate.getHotelCode())) {
                errors.add(ValidationError.businessRuleViolation(
                        roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "ratePlan", "Rate plan not found or doesn't belong to hotel", null));
            }
        }
    }

    /**
     * NIGHT TYPE VALIDATION - Migrado de ImportBookingNightTypeValidator
     */
    private void validateNightType(UnifiedRoomRateDto roomRate, ReferenceDataCache cache, List<ValidationError> errors) {
        String nightTypeCode = roomRate.getNightType();

        // Verificar si night type es obligatorio según la agencia
        boolean isNightTypeMandatory = isNightTypeMandatory(roomRate, cache);

        if (isNightTypeMandatory && isNullOrEmpty(nightTypeCode)) {
            errors.add(ValidationError.fieldRequired(roomRate.getSourceType(), roomRate.getSourceIdentifier(), "nightType"));
            return;
        }

        if (!isNullOrEmpty(nightTypeCode)) {
            String upperNightType = nightTypeCode.toUpperCase().trim();
            if (!cache.hasValidNightType(upperNightType)) {
                errors.add(ValidationError.businessRuleViolation(
                        roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "nightType", "Night type not found", null));
            }
        }
    }

    /**
     * CHECK-IN/CHECK-OUT VALIDATION - Migrado de ImportBookingCheckInValidator y CheckOutValidator
     */
    private void validateCheckInOut(UnifiedRoomRateDto roomRate, List<ValidationError> errors) {
        String checkIn = roomRate.getCheckIn();
        String checkOut = roomRate.getCheckOut();

        // Validar Check-In
        if (isNullOrEmpty(checkIn)) {
            errors.add(ValidationError.fieldRequired(roomRate.getSourceType(), roomRate.getSourceIdentifier(), "checkIn"));
        } else if (!isValidDateFormat(checkIn)) {
            errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "checkIn", "Invalid date format"));
        }

        // Validar Check-Out
        if (isNullOrEmpty(checkOut)) {
            errors.add(ValidationError.fieldRequired(roomRate.getSourceType(), roomRate.getSourceIdentifier(), "checkOut"));
        } else if (!isValidDateFormat(checkOut)) {
            errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "checkOut", "Invalid date format"));
        }

        // Validar que check-out sea después de check-in
        if (!isNullOrEmpty(checkIn) && !isNullOrEmpty(checkOut)) {
            try {
                LocalDateTime checkInDate = DateUtil.parseDateToDateTime(checkIn);
                LocalDateTime checkOutDate = DateUtil.parseDateToDateTime(checkOut);

                if (!checkOutDate.isAfter(checkInDate)) {
                    errors.add(ValidationError.businessRuleViolation(
                            roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                            "checkOut", "Check-out must be after check-in", null));
                }
            } catch (Exception e) {
                errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "checkIn/checkOut", "Invalid date format"));
            }
        }
    }

    /**
     * BOOKING DATE VALIDATION - Migrado de ImportBookingDateValidator
     */
    private void validateBookingDate(UnifiedRoomRateDto roomRate, List<ValidationError> errors) {
        String bookingDate = roomRate.getBookingDate();

        if (!isNullOrEmpty(bookingDate)) {
            if (!isValidDateFormat(bookingDate)) {
                errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "bookingDate", "Invalid date format"));
                return;
            }

            try {
                LocalDate dateToValidate = DateUtil.parseDateToLocalDate(bookingDate);
                if (dateToValidate.isAfter(LocalDate.now())) {
                    errors.add(ValidationError.businessRuleViolation(
                            roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                            "bookingDate", "Booking date cannot be in the future", null));
                }
            } catch (Exception e) {
                errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "bookingDate", "Invalid date format"));
            }
        }
    }

    /**
     * TRANSACTION DATE VALIDATION - Migrado de ImportBookingTransactionDateValidator
     */
    private void validateTransactionDate(UnifiedRoomRateDto roomRate, ReferenceDataCache cache, List<ValidationError> errors) {
        String transactionDate = roomRate.getTransactionDate();

        if (isNullOrEmpty(transactionDate)) {
            errors.add(ValidationError.fieldRequired(roomRate.getSourceType(), roomRate.getSourceIdentifier(), "transactionDate"));
            return;
        }

        if (!isValidDateFormat(transactionDate)) {
            errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "transactionDate", "Invalid date format"));
            return;
        }

        try {
            LocalDate dateToValidate = DateUtil.parseDateToLocalDate(transactionDate);

            // No puede ser fecha futura
            if (dateToValidate.isAfter(LocalDate.now())) {
                errors.add(ValidationError.businessRuleViolation(
                        roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "transactionDate", "Transaction date cannot be in the future", null));
            }

            // Validar close operations
            if (!cache.isDateAllowedForProcessing(roomRate.getHotelCode(), transactionDate)) {
                errors.add(ValidationError.businessRuleViolation(
                        roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "transactionDate", "Transaction date is out of close operation period", null));
            }

        } catch (Exception e) {
            errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "transactionDate", "Invalid date format"));
        }
    }

    /**
     * AMOUNTS VALIDATION - Migrado de ImportBookingInvoiceAmountValidator
     */
    private void validateAmounts(UnifiedRoomRateDto roomRate, List<ValidationError> errors) {
        // Invoice Amount validation
        if (roomRate.getInvoiceAmount() == null) {
            errors.add(ValidationError.fieldRequired(roomRate.getSourceType(), roomRate.getSourceIdentifier(), "invoiceAmount"));
        } else if (roomRate.getInvoiceAmount() <= 0) {
            errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "invoiceAmount", "Invoice amount must be greater than 0"));
        }

        // Adults validation (no puede ser null o <= 0 para imports normales)
        if (roomRate.getAdults() == null) {
            errors.add(ValidationError.fieldRequired(roomRate.getSourceType(), roomRate.getSourceIdentifier(), "adults"));
        } else if (roomRate.getAdults() < 0) {
            errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "adults", "Adults cannot be negative"));
        }

        // Children validation
        if (roomRate.getChildren() != null && roomRate.getChildren() < 0) {
            errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "children", "Children cannot be negative"));
        }

        // Nights validation
        if (roomRate.getNights() != null && roomRate.getNights() <= 0) {
            errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "nights", "Nights must be greater than 0"));
        }
    }

    /**
     * HOTEL INVOICE AMOUNT VALIDATION - Migrado de ImportBookingHotelInvoiceAmountValidator
     */
    private void validateHotelInvoiceAmount(UnifiedRoomRateDto roomRate, ReferenceDataCache cache, List<ValidationError> errors) {
        Optional<ManageHotelDto> hotelOpt = cache.getHotelIfAllowed(roomRate.getHotelCode());

        if (hotelOpt.isPresent()) {
            ManageHotelDto hotel = hotelOpt.get();

            // Solo validar si el hotel requiere flat rate
            if (hotel.isRequiresFlatRate()) {
                if (roomRate.getHotelInvoiceAmount() == null) {
                    errors.add(ValidationError.fieldRequired(roomRate.getSourceType(), roomRate.getSourceIdentifier(), "hotelInvoiceAmount"));
                } else if (roomRate.getHotelInvoiceAmount() <= 0) {
                    errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                            "hotelInvoiceAmount", "Hotel invoice amount must be greater than 0"));
                }
            }
        }
    }

    /**
     * NAMES VALIDATION - Migrado de ImportBookingNameValidator
     */
    private void validateNames(UnifiedRoomRateDto roomRate, List<ValidationError> errors) {
        String firstName = roomRate.getFirstName();
        String lastName = roomRate.getLastName();

        if (isNullOrEmpty(firstName) && isNullOrEmpty(lastName)) {
            errors.add(ValidationError.businessRuleViolation(
                    roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "firstName/lastName", "At least first name or last name must be provided", null));
        }
    }

    /**
     * AMOUNT PAX VALIDATION - Migrado de ImportBookingAmountPaxValidator
     */
    private void validateAmountPAX(UnifiedRoomRateDto roomRate, List<ValidationError> errors) {
        Double amountPAX = roomRate.getAmountPAX();

        if (amountPAX != null) {
            if (amountPAX == 0) {
                errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "amountPAX", "Amount PAX must not be 0"));
                return;
            }

            double adults = roomRate.getAdults() != null ? roomRate.getAdults() : 0;
            double children = roomRate.getChildren() != null ? roomRate.getChildren() : 0;

            if (adults + children != amountPAX) {
                errors.add(ValidationError.businessRuleViolation(
                        roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "amountPAX", "Amount PAX is different from total people in reservation", null));
            }
        }
    }

    /**
     * HOTEL BOOKING NUMBER VALIDATION - Migrado de ImportBookingHotelBookingNoValidator
     */
    private void validateHotelBookingNumber(UnifiedRoomRateDto roomRate, List<ValidationError> errors) {
        String hotelBookingNumber = roomRate.getHotelBookingNumber();

        if (isNullOrEmpty(hotelBookingNumber)) {
            errors.add(ValidationError.fieldRequired(roomRate.getSourceType(), roomRate.getSourceIdentifier(), "hotelBookingNumber"));
            return;
        }

        // Validar formato de reservation number
        String RESERVATION_NUMBER_REGEX = "^(I|G)(\\s)+(\\d)+(\\s)+(\\d)+";
        if (!hotelBookingNumber.matches(RESERVATION_NUMBER_REGEX)) {
            errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "hotelBookingNumber", "Hotel booking number has invalid format"));
        }
    }

    /**
     * IMPORT TYPE VALIDATION - Migrado de ImportBookingTypeValidator
     */
    private void validateImportType(UnifiedRoomRateDto roomRate, ReferenceDataCache cache, List<ValidationError> errors) {
        Optional<ManageHotelDto> hotelOpt = cache.getHotelIfAllowed(roomRate.getHotelCode());

        if (hotelOpt.isPresent()) {
            ManageHotelDto hotel = hotelOpt.get();

            // Aquí necesitarías el import type del contexto - lo podrías agregar al UnifiedRoomRateDto
            // Por ahora, validación básica de hotel virtual
            String sourceType = roomRate.getSourceType();

            if ("VIRTUAL".equals(sourceType) && !hotel.isVirtual()) {
                errors.add(ValidationError.businessRuleViolation(
                        roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "importType", "Hotel is not virtual but import type is VIRTUAL", null));
            } else if ("NO_VIRTUAL".equals(sourceType) && hotel.isVirtual()) {
                errors.add(ValidationError.businessRuleViolation(
                        roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "importType", "Hotel is virtual but import type is NO_VIRTUAL", null));
            }
        }
    }

    /**
     * DUPLICATES VALIDATION - Migrado de ImportBookingDuplicateValidator
     * Valida duplicados usando el cache precargado
     */
    private void validateDuplicates(UnifiedRoomRateDto roomRate, ReferenceDataCache cache, List<ValidationError> errors) {
        String hotelBookingNumber = roomRate.getHotelBookingNumber();

        if (!isNullOrEmpty(hotelBookingNumber)) {
            // Verificar duplicados en BD usando cache
            if (cache.isBookingNumberDuplicate(hotelBookingNumber)) {
                errors.add(ValidationError.businessRuleViolation(
                        roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "hotelBookingNumber", "Record has already been imported", null));
            }
        }

        // Validar hotel invoice number para hoteles virtuales
        String hotelInvoiceNumber = roomRate.getHotelInvoiceNumber();
        if (!isNullOrEmpty(hotelInvoiceNumber)) {
            Optional<ManageHotelDto> hotelOpt = cache.getHotelIfAllowed(roomRate.getHotelCode());
            if (hotelOpt.isPresent() && hotelOpt.get().isVirtual()) {
                if (cache.isHotelInvoiceNumberDuplicate(hotelInvoiceNumber)) {
                    errors.add(ValidationError.businessRuleViolation(
                            roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                            "hotelInvoiceNumber", "Hotel invoice number already exists", null));
                }
            }
        }
    }

    // === VALIDACIONES DE GRUPO (BUSINESS RULES) ===

    /**
     * REGLA CRÍTICA: Al menos un adulto en todo el booking
     */
    private void validateAdultsInBooking(String bookingKey, List<UnifiedRoomRateDto> bookingRates, List<ValidationError> errors) {
        double totalAdults = bookingRates.stream()
                .mapToDouble(rate -> Optional.ofNullable(rate.getAdults()).orElse(0.0))
                .sum();

        if (totalAdults <= 0) {
            // Crear error para todas las tarifas del grupo
            bookingRates.forEach(rate -> {
                errors.add(ValidationError.businessRuleViolation(
                        rate.getSourceType(), rate.getSourceIdentifier(),
                        "adults", "Booking group must have at least one adult across all room rates",
                        bookingKey));
            });
        }
    }

    /**
     * Validar consistencia en fechas dentro del booking
     */
    private void validateDateConsistency(String bookingKey, List<UnifiedRoomRateDto> bookingRates, List<ValidationError> errors) {
        // Verificar que todas las tarifas del booking tengan la misma transaction date
        Set<String> transactionDates = bookingRates.stream()
                .map(UnifiedRoomRateDto::getTransactionDate)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (transactionDates.size() > 1) {
            bookingRates.forEach(rate -> {
                errors.add(ValidationError.businessRuleViolation(
                        rate.getSourceType(), rate.getSourceIdentifier(),
                        "transactionDate", "All room rates in booking must have same transaction date",
                        bookingKey));
            });
        }
    }

    /**
     * Validar montos y cálculos
     */
    private void validateAmounts(String bookingKey, List<UnifiedRoomRateDto> bookingRates, List<ValidationError> errors) {
        // Verificar que todos los montos sean positivos
        bookingRates.forEach(rate -> {
            if (rate.getInvoiceAmount() != null && rate.getInvoiceAmount() <= 0) {
                errors.add(ValidationError.invalidValue(
                        rate.getSourceType(), rate.getSourceIdentifier(),
                        "invoiceAmount", String.valueOf(rate.getInvoiceAmount())));
            }

            if (rate.getHotelInvoiceAmount() != null && rate.getHotelInvoiceAmount() < 0) {
                errors.add(ValidationError.invalidValue(
                        rate.getSourceType(), rate.getSourceIdentifier(),
                        "hotelInvoiceAmount", String.valueOf(rate.getHotelInvoiceAmount())));
            }
        });
    }

    /**
     * Validar duplicados dentro del grupo
     */
    private void validateDuplicatesInGroup(String bookingKey, List<UnifiedRoomRateDto> bookingRates, List<ValidationError> errors) {
        // Verificar duplicados por características únicas (room number + dates)
        Map<String, List<UnifiedRoomRateDto>> duplicateCheck = bookingRates.stream()
                .filter(rate -> rate.getRoomNumber() != null)
                .collect(Collectors.groupingBy(rate ->
                        rate.getRoomNumber() + "|" + rate.getCheckIn() + "|" + rate.getCheckOut()));

        duplicateCheck.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .forEach(entry -> {
                    entry.getValue().forEach(rate -> {
                        errors.add(ValidationError.businessRuleViolation(
                                rate.getSourceType(), rate.getSourceIdentifier(),
                                "roomNumber", "Duplicate room assignment in same period",
                                bookingKey));
                    });
                });
    }

    /**
     * Validar reglas específicas según el tipo de agencia
     */
    private void validateAgencySpecificRules(String bookingKey, List<UnifiedRoomRateDto> bookingRates, ReferenceDataCache cache, List<ValidationError> errors) {
        // Obtener la agencia del primer rate (todas deberían ser iguales en el grupo)
        UnifiedRoomRateDto firstRate = bookingRates.get(0);
        cache.getAgencyIfAllowed(firstRate.getAgencyCode()).ifPresent(agency -> {

            // Aplicar reglas según el tipo de generación de la agencia
            switch (agency.getGenerationType()) {
                case ByCoupon:
                    validateCouponRules(bookingKey, bookingRates, errors);
                    break;
                case ByBooking:
                    validateBookingRules(bookingKey, bookingRates, errors);
                    break;
                // Agregar más tipos según necesidad
            }
        });
    }

    private void validateCouponRules(String bookingKey, List<UnifiedRoomRateDto> bookingRates, List<ValidationError> errors) {
        // Para agencias ByCoupon, validar que tengan coupon
        bookingRates.forEach(rate -> {
            if (isNullOrEmpty(rate.getCoupon())) {
                errors.add(ValidationError.businessRuleViolation(
                        rate.getSourceType(), rate.getSourceIdentifier(),
                        "coupon", "Coupon is required for agencies with ByCoupon generation type",
                        bookingKey));
            }
        });
    }

    private void validateBookingRules(String bookingKey, List<UnifiedRoomRateDto> bookingRates, List<ValidationError> errors) {
        // Para agencias ByBooking, validar que tengan hotel booking number
        bookingRates.forEach(rate -> {
            if (isNullOrEmpty(rate.getHotelBookingNumber())) {
                errors.add(ValidationError.businessRuleViolation(
                        rate.getSourceType(), rate.getSourceIdentifier(),
                        "hotelBookingNumber", "Hotel booking number is required for agencies with ByBooking generation type",
                        bookingKey));
            }
        });
    }

    // === MÉTODOS AUXILIARES ===

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Verifica si night type es obligatorio según la configuración de la agencia
     */
    private boolean isNightTypeMandatory(UnifiedRoomRateDto roomRate, ReferenceDataCache cache) {
        Optional<ManageAgencyDto> agencyOpt = cache.getAgencyIfAllowed(roomRate.getAgencyCode());

        if (agencyOpt.isPresent()) {
            ManageAgencyDto agency = agencyOpt.get();
            ManageClientDto client = agency.getClient();

            return client != null &&
                    client.getIsNightType() != null &&
                    client.getIsNightType();
        }

        return false;
    }

    /**
     * Valida formato de fecha usando los formatos permitidos
     */
    private boolean isValidDateFormat(String date) {
        if (isNullOrEmpty(date)) {
            return false;
        }

        String[] validFormats = {"yyyyMMdd", "MM/dd/yyyy", "yyyy-MM-dd"};

        for (String format : validFormats) {
            try {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(format);
                java.time.LocalDate.parse(date, formatter);
                return true;
            } catch (Exception e) {
                // Continuar con el siguiente formato
            }
        }

        return false;
    }

    /**
     * Placeholder para validaciones requeridas pero no implementadas todavía
     */
    private void validateRequiredFields(UnifiedRoomRateDto roomRate, List<ValidationError> errors) {
        // Las validaciones específicas ya están en los métodos individuales
        // Este método se mantiene para compatibilidad pero ya no se usa
    }

    private void validateDataFormats(UnifiedRoomRateDto roomRate, List<ValidationError> errors) {
        // Las validaciones específicas ya están en los métodos individuales
        // Este método se mantiene para compatibilidad pero ya no se usa
    }
}