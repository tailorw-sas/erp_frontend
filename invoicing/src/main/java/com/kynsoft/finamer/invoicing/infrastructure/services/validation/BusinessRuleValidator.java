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
 * Business rule validator that handles complex dependencies between room rates.
 * Validates in-memory with pre-filled data and applies cross-rate rules such as:
 * - At least one adult per booking
 * - Consistency in dates and amounts
 * - Master data validations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessRuleValidator {

    @Qualifier("validationExecutor")
    private final TaskExecutor validationExecutor;

    /**
     * Executes comprehensive validation including complex business rules.
     * Validates the provided list of room rates using the preloaded reference data cache.
     * Performs both individual and group validations, and returns all found errors as well as valid room rates.
     *
     * @param roomRates List of room rates to validate
     * @param cache Preloaded reference data cache
     * @return Result containing valid room rates and all found errors
     */
    public Mono<ValidationResult> validateWithBusinessRules(List<UnifiedRoomRateDto> roomRates,
                                                            ReferenceDataCache cache) {
        log.info("Starting business rule validation for {} room rates", roomRates.size());
        long startTime = System.currentTimeMillis();

        return Mono.fromCallable(() -> {

                    // Use thread-safe structures for parallelization
                    List<ValidationError> allErrors = new CopyOnWriteArrayList<>();
                    List<UnifiedRoomRateDto> validRoomRates = new CopyOnWriteArrayList<>();

                    // 1. Individual validations first (can be executed in parallel)
                    Map<String, List<ValidationError>> individualErrors = validateIndividualRoomRates(roomRates, cache);

                    // 2. If there are critical individual errors, do not continue with group validations
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

                    // 3. Add warnings from individual validations
                    List<ValidationError> warnings = individualErrors.values().stream()
                            .flatMap(List::stream)
                            .filter(error -> !error.isBlocking())
                            .collect(Collectors.toList());
                    allErrors.addAll(warnings);

                    // 4. Group room rates according to business logic for cross-rate validations
                    Map<String, List<UnifiedRoomRateDto>> bookingGroups = groupRoomRatesForBookingValidation(roomRates);
                    log.debug("Grouped {} room rates into {} booking groups", roomRates.size(), bookingGroups.size());

                    // 5. Validate business rules by group (can be executed in parallel)
                    List<ValidationError> businessRuleErrors = validateBusinessRulesByGroup(bookingGroups, cache);
                    allErrors.addAll(businessRuleErrors);

                    // 6. If there are no blocking errors, all room rates are valid for processing
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
     * Validates each room rate individually against master data.
     * Includes all optimized validations from the current system.
     *
     * @param roomRates List of room rates to validate
     * @param cache Reference data cache for master data lookups
     * @return Map of room rate unique identifiers to their found validation errors
     */
    private Map<String, List<ValidationError>> validateIndividualRoomRates(List<UnifiedRoomRateDto> roomRates,
                                                                           ReferenceDataCache cache) {
        Map<String, List<ValidationError>> errorsByRoomRate = new ConcurrentHashMap<>();

        // Parallel processing of individual validations
        roomRates.parallelStream().forEach(roomRate -> {
            List<ValidationError> roomRateErrors = new ArrayList<>();
            String roomRateKey = roomRate.getUniqueIdentifier();

            // === BASIC VALIDATIONS ===
            validateRequiredFields(roomRate, roomRateErrors);
            validateDataFormats(roomRate, roomRateErrors);

            // === MASTER DATA VALIDATIONS ===
            validateHotel(roomRate, cache, roomRateErrors);
            validateAgency(roomRate, cache, roomRateErrors);
            validateRoomType(roomRate, cache, roomRateErrors);
            validateRatePlan(roomRate, cache, roomRateErrors);
            validateNightType(roomRate, cache, roomRateErrors);

            // === DATE VALIDATIONS ===
            validateCheckInOut(roomRate, roomRateErrors);
            validateBookingDate(roomRate, roomRateErrors);
            validateTransactionDate(roomRate, cache, roomRateErrors);

            // === AMOUNT VALIDATIONS ===
            validateAmounts(roomRate, roomRateErrors);
            validateHotelInvoiceAmount(roomRate, cache, roomRateErrors);

            // === SPECIFIC VALIDATIONS ===
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
     * Groups room rates according to business rules to form bookings.
     *
     * @param roomRates List of room rates to group
     * @return Map of booking group keys to lists of room rates in each group
     */
    private Map<String, List<UnifiedRoomRateDto>> groupRoomRatesForBookingValidation(List<UnifiedRoomRateDto> roomRates) {
        return roomRates.stream()
                .collect(Collectors.groupingBy(this::calculateBookingGroupKey));
    }

    /**
     * Calculates the grouping key for bookings.
     * This logic determines which room rates form a booking group.
     *
     * @param roomRate Room rate to calculate the group key for
     * @return String representing the booking group key
     */
    private String calculateBookingGroupKey(UnifiedRoomRateDto roomRate) {
        // Grouping rule: Hotel + Agency + Hotel Booking Number + Transaction Date
        return String.format("%s|%s|%s|%s",
                Objects.toString(roomRate.getHotelCode(), ""),
                Objects.toString(roomRate.getAgencyCode(), ""),
                Objects.toString(roomRate.getHotelBookingNumber(), ""),
                Objects.toString(roomRate.getTransactionDate(), ""));
    }

    /**
     * Validates business rules that apply to groups of room rates (bookings).
     *
     * @param bookingGroups Map of booking group keys to lists of room rates
     * @param cache Reference data cache for lookups
     * @return List of validation errors found at the group level
     */
    private List<ValidationError> validateBusinessRulesByGroup(Map<String, List<UnifiedRoomRateDto>> bookingGroups,
                                                               ReferenceDataCache cache) {
        List<ValidationError> allGroupErrors = new CopyOnWriteArrayList<>();

        // Validate each group in parallel
        bookingGroups.entrySet().parallelStream().forEach(entry -> {
            String bookingKey = entry.getKey();
            List<UnifiedRoomRateDto> bookingRates = entry.getValue();

            List<ValidationError> groupErrors = new ArrayList<>();

            // CRITICAL RULE: At least one adult in the entire booking
            validateAdultsInBooking(bookingKey, bookingRates, groupErrors);

            // RULE: Consistency in booking dates
            validateDateConsistency(bookingKey, bookingRates, groupErrors);

            // RULE: Validate amounts and calculations
            validateAmounts(bookingKey, bookingRates, groupErrors);

            // RULE: Validate duplicates within the group
            validateDuplicatesInGroup(bookingKey, bookingRates, groupErrors);

            // RULE: Validate specific logic according to agency type
            validateAgencySpecificRules(bookingKey, bookingRates, cache, groupErrors);

            allGroupErrors.addAll(groupErrors);
        });

        return allGroupErrors;
    }

    // === COMPLETE INDIVIDUAL VALIDATIONS ===

    /**
     * HOTEL VALIDATION - Migrated from ImportBookingHotelValidator.
     * Validates that the hotel exists and is active.
     *
     * @param roomRate Room rate to validate
     * @param cache Reference data cache
     * @param errors List to collect validation errors
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
     * AGENCY VALIDATION - Migrated from ImportBookingAgencyValidator.
     * Validates that the agency exists and is active.
     *
     * @param roomRate Room rate to validate
     * @param cache Reference data cache
     * @param errors List to collect validation errors
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
     * ROOM TYPE VALIDATION - Migrated from ImportBookingRoomTypeValidator.
     * Validates that the room type exists and belongs to the hotel.
     *
     * @param roomRate Room rate to validate
     * @param cache Reference data cache
     * @param errors List to collect validation errors
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
     * RATE PLAN VALIDATION - Migrated from ImportBookingRatePlanValidator.
     * Validates that the rate plan exists and belongs to the hotel.
     *
     * @param roomRate Room rate to validate
     * @param cache Reference data cache
     * @param errors List to collect validation errors
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
     * NIGHT TYPE VALIDATION - Migrated from ImportBookingNightTypeValidator.
     * Validates the night type if required by the agency configuration.
     *
     * @param roomRate Room rate to validate
     * @param cache Reference data cache
     * @param errors List to collect validation errors
     */
    private void validateNightType(UnifiedRoomRateDto roomRate, ReferenceDataCache cache, List<ValidationError> errors) {
        String nightTypeCode = roomRate.getNightType();

        // Check if night type is mandatory according to agency
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
     * CHECK-IN/CHECK-OUT VALIDATION - Migrated from ImportBookingCheckInValidator and CheckOutValidator.
     * Validates presence and formats of check-in/check-out and their logical order.
     *
     * @param roomRate Room rate to validate
     * @param errors List to collect validation errors
     */
    private void validateCheckInOut(UnifiedRoomRateDto roomRate, List<ValidationError> errors) {
        String checkIn = roomRate.getCheckIn();
        String checkOut = roomRate.getCheckOut();

        // Validate Check-In
        if (isNullOrEmpty(checkIn)) {
            errors.add(ValidationError.fieldRequired(roomRate.getSourceType(), roomRate.getSourceIdentifier(), "checkIn"));
        } else if (!isValidDateFormat(checkIn)) {
            errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "checkIn", "Invalid date format"));
        }

        // Validate Check-Out
        if (isNullOrEmpty(checkOut)) {
            errors.add(ValidationError.fieldRequired(roomRate.getSourceType(), roomRate.getSourceIdentifier(), "checkOut"));
        } else if (!isValidDateFormat(checkOut)) {
            errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "checkOut", "Invalid date format"));
        }

        // Validate that check-out is after check-in
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
     * BOOKING DATE VALIDATION - Migrated from ImportBookingDateValidator.
     * Validates that the booking date is valid and not in the future.
     *
     * @param roomRate Room rate to validate
     * @param errors List to collect validation errors
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
     * TRANSACTION DATE VALIDATION - Migrated from ImportBookingTransactionDateValidator.
     * Validates that the transaction date is valid, not in the future, and allowed for processing.
     *
     * @param roomRate Room rate to validate
     * @param cache Reference data cache
     * @param errors List to collect validation errors
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

            // Cannot be a future date
            if (dateToValidate.isAfter(LocalDate.now())) {
                errors.add(ValidationError.businessRuleViolation(
                        roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "transactionDate", "Transaction date cannot be in the future", null));
            }

            // Validate close operations
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
     * AMOUNTS VALIDATION - Migrated from ImportBookingInvoiceAmountValidator.
     * Validates invoice amounts, adults, children, and nights for correctness and positivity.
     *
     * @param roomRate Room rate to validate
     * @param errors List to collect validation errors
     */
    private void validateAmounts(UnifiedRoomRateDto roomRate, List<ValidationError> errors) {
        // Invoice Amount validation
        if (roomRate.getInvoiceAmount() == null) {
            errors.add(ValidationError.fieldRequired(roomRate.getSourceType(), roomRate.getSourceIdentifier(), "invoiceAmount"));
        } else if (roomRate.getInvoiceAmount() <= 0) {
            errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "invoiceAmount", "Invoice amount must be greater than 0"));
        }

        // Adults validation (cannot be null or <= 0 for normal imports)
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
     * HOTEL INVOICE AMOUNT VALIDATION - Migrated from ImportBookingHotelInvoiceAmountValidator.
     * Validates hotel invoice amounts when required by the hotel configuration.
     *
     * @param roomRate Room rate to validate
     * @param cache Reference data cache
     * @param errors List to collect validation errors
     */
    private void validateHotelInvoiceAmount(UnifiedRoomRateDto roomRate, ReferenceDataCache cache, List<ValidationError> errors) {
        Optional<ManageHotelDto> hotelOpt = cache.getHotelIfAllowed(roomRate.getHotelCode());

        if (hotelOpt.isPresent()) {
            ManageHotelDto hotel = hotelOpt.get();

            // Only validate if the hotel requires flat rate
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
     * NAMES VALIDATION - Migrated from ImportBookingNameValidator.
     * Validates that at least the first name or last name is provided.
     *
     * @param roomRate Room rate to validate
     * @param errors List to collect validation errors
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
     * AMOUNT PAX VALIDATION - Migrated from ImportBookingAmountPaxValidator.
     * Validates that amountPAX is not zero and matches the sum of adults and children.
     *
     * @param roomRate Room rate to validate
     * @param errors List to collect validation errors
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
     * HOTEL BOOKING NUMBER VALIDATION - Migrated from ImportBookingHotelBookingNoValidator.
     * Validates presence and format of the hotel booking number.
     *
     * @param roomRate Room rate to validate
     * @param errors List to collect validation errors
     */
    private void validateHotelBookingNumber(UnifiedRoomRateDto roomRate, List<ValidationError> errors) {
        String hotelBookingNumber = roomRate.getHotelBookingNumber();

        if (isNullOrEmpty(hotelBookingNumber)) {
            errors.add(ValidationError.fieldRequired(roomRate.getSourceType(), roomRate.getSourceIdentifier(), "hotelBookingNumber"));
            return;
        }

        // Validate reservation number format
        String RESERVATION_NUMBER_REGEX = "^(I|G)(\\s)+(\\d)+(\\s)+(\\d)+";
        if (!hotelBookingNumber.matches(RESERVATION_NUMBER_REGEX)) {
            errors.add(ValidationError.invalidValue(roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                    "hotelBookingNumber", "Hotel booking number has invalid format"));
        }
    }

    /**
     * IMPORT TYPE VALIDATION - Migrated from ImportBookingTypeValidator.
     * Validates the import type in relation to the hotel type (virtual or not).
     *
     * @param roomRate Room rate to validate
     * @param cache Reference data cache
     * @param errors List to collect validation errors
     */
    private void validateImportType(UnifiedRoomRateDto roomRate, ReferenceDataCache cache, List<ValidationError> errors) {
        Optional<ManageHotelDto> hotelOpt = cache.getHotelIfAllowed(roomRate.getHotelCode());

        if (hotelOpt.isPresent()) {
            ManageHotelDto hotel = hotelOpt.get();

            // Here you would need the import type from the context - you could add it to UnifiedRoomRateDto
            // For now, basic validation for virtual hotel
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
     * DUPLICATES VALIDATION - Migrated from ImportBookingDuplicateValidator.
     * Validates duplicates using the preloaded cache.
     *
     * @param roomRate Room rate to validate
     * @param cache Reference data cache
     * @param errors List to collect validation errors
     */
    private void validateDuplicates(UnifiedRoomRateDto roomRate, ReferenceDataCache cache, List<ValidationError> errors) {
        String hotelBookingNumber = roomRate.getHotelBookingNumber();

        if (!isNullOrEmpty(hotelBookingNumber)) {
            // Check for duplicates in the database using the cache
            if (cache.isBookingNumberDuplicate(hotelBookingNumber)) {
                errors.add(ValidationError.businessRuleViolation(
                        roomRate.getSourceType(), roomRate.getSourceIdentifier(),
                        "hotelBookingNumber", "Record has already been imported", null));
            }
        }

        // Validate hotel invoice number for virtual hotels
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

    // === GROUP VALIDATIONS (BUSINESS RULES) ===

    /**
     * CRITICAL RULE: At least one adult in the entire booking group.
     * Adds an error if the total number of adults across all room rates is zero or less.
     *
     * @param bookingKey Booking group key
     * @param bookingRates List of room rates in the group
     * @param errors List to collect validation errors
     */
    private void validateAdultsInBooking(String bookingKey, List<UnifiedRoomRateDto> bookingRates, List<ValidationError> errors) {
        double totalAdults = bookingRates.stream()
                .mapToDouble(rate -> Optional.ofNullable(rate.getAdults()).orElse(0.0))
                .sum();

        if (totalAdults <= 0) {
            // Create error for all rates in the group
            bookingRates.forEach(rate -> {
                errors.add(ValidationError.businessRuleViolation(
                        rate.getSourceType(), rate.getSourceIdentifier(),
                        "adults", "Booking group must have at least one adult across all room rates",
                        bookingKey));
            });
        }
    }

    /**
     * Validates consistency of dates within a booking group.
     * Ensures that all room rates in the group have the same transaction date.
     *
     * @param bookingKey Booking group key
     * @param bookingRates List of room rates in the group
     * @param errors List to collect validation errors
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
     * Validates amounts and calculations within a booking group.
     * Checks that all amounts are positive.
     *
     * @param bookingKey Booking group key
     * @param bookingRates List of room rates in the group
     * @param errors List to collect validation errors
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
     * Validates duplicates within a booking group.
     * Checks for duplicate room assignments in the same period.
     *
     * @param bookingKey Booking group key
     * @param bookingRates List of room rates in the group
     * @param errors List to collect validation errors
     */
    private void validateDuplicatesInGroup(String bookingKey, List<UnifiedRoomRateDto> bookingRates, List<ValidationError> errors) {
        // Check for duplicates by unique characteristics (room number + dates)
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
     * Validates specific rules according to the agency type.
     * Applies custom rules depending on the agency's generation type.
     *
     * @param bookingKey Booking group key
     * @param bookingRates List of room rates in the group
     * @param cache Reference data cache
     * @param errors List to collect validation errors
     */
    private void validateAgencySpecificRules(String bookingKey, List<UnifiedRoomRateDto> bookingRates, ReferenceDataCache cache, List<ValidationError> errors) {
        // Obtener la agencia del primer rate (todas deberían ser iguales en el grupo)
        UnifiedRoomRateDto firstRate = bookingRates.get(0);
        cache.getAgencyIfAllowed(firstRate.getAgencyCode()).ifPresent(agency -> {

            // Apply rules according to the agency's generation type
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
        // For ByCoupon agencies, validate that coupon is present
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
        // For ByBooking agencies, validate that hotel booking number is present
        bookingRates.forEach(rate -> {
            if (isNullOrEmpty(rate.getHotelBookingNumber())) {
                errors.add(ValidationError.businessRuleViolation(
                        rate.getSourceType(), rate.getSourceIdentifier(),
                        "hotelBookingNumber", "Hotel booking number is required for agencies with ByBooking generation type",
                        bookingKey));
            }
        });
    }

    // === HELPER METHODS ===

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Checks if night type is mandatory according to the agency's configuration.
     *
     * @param roomRate Room rate to check
     * @param cache Reference data cache
     * @return true if night type is mandatory, false otherwise
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
     * Validates the date format using allowed patterns.
     *
     * @param date Date string to validate
     * @return true if date matches any allowed format, false otherwise
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
                // Continue with the next format
            }
        }

        return false;
    }

    /**
     * Placeholder for required field validations not yet implemented.
     * Specific validations are already handled in individual methods.
     *
     * @param roomRate Room rate to validate
     * @param errors List to collect validation errors
     */
    private void validateRequiredFields(UnifiedRoomRateDto roomRate, List<ValidationError> errors) {
        // Las validaciones específicas ya están en los métodos individuales
        // Este método se mantiene para compatibilidad pero ya no se usa
    }

    private void validateDataFormats(UnifiedRoomRateDto roomRate, List<ValidationError> errors) {
        // Specific validations are already handled in individual methods
        // This method is kept for compatibility but is no longer used
    }
}