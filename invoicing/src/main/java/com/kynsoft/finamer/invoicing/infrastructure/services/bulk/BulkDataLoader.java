package com.kynsoft.finamer.invoicing.infrastructure.services.bulk;

import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dto.cache.ReferenceDataCache;
import com.kynsoft.finamer.invoicing.domain.dto.roomrate.UnifiedRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.dto.validation.DuplicateValidationResult;
import com.kynsoft.finamer.invoicing.domain.dto.validation.HotelBookingCombinationDto;
import com.kynsoft.finamer.invoicing.domain.dto.validation.HotelInvoiceCombinationDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.invoicing.domain.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Optimized service for bulk loading of all necessary reference data
 * to validate and process room rates. Eliminates N+1 queries and improves performance.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BulkDataLoader {

    private final IManageHotelService hotelService;
    private final IManageAgencyService agencyService;
    private final IManageRoomTypeService roomTypeService;
    private final IManageRatePlanService ratePlanService;
    private final IManageNightTypeService nightTypeService;
    private final IInvoiceCloseOperationService closeOperationService;
    private final IManageEmployeeService employeeService;
    private final IManageBookingService bookingService;
    private final IManageInvoiceStatusService invoiceStatusService;
    private final IManageInvoiceTypeService invoiceTypeService;

    @Qualifier("ioExecutor")
    private final TaskExecutor ioExecutor;

    /**
     * Preloads all the reference data needed to process a list of room rates
     *
     * @param unifiedDtos List of room rates to process
     * @param importType Import type (VIRTUAL, NO_VIRTUAL, INNSIST)
     * @param employeeId ID of the employee performing the import
     * @return Full cache with all the necessary data
     */
    public Mono<ReferenceDataCache> preloadReferenceData(List<UnifiedRoomRateDto> unifiedDtos, ImportType importType, String employeeId) {
        log.info("Starting bulk data loading for {} room rates, importType: {}, employee: {}", unifiedDtos.size(), importType, employeeId);
        long startTime = System.currentTimeMillis();

        return Mono.fromCallable(() -> {

                    // 1. Extract all unique codes from Excel/input data
                    DataCodes codes = extractUniqueCodes(unifiedDtos);
                    log.debug("Extracted codes - Hotels: {}, Agencies: {}, RoomTypes: {}, RatePlans: {}, NightTypes: {}",
                            codes.hotelCodes.size(), codes.agencyCodes.size(),
                            codes.roomTypeCodes.size(), codes.ratePlanCodes.size(), codes.nightTypeCodes.size());

                    // 2. Load user permissions first (needed to filter data)
                    UserPermissions userPermissions = loadUserPermissions(employeeId);
                    log.debug("User permissions loaded - Hotels: {}, Agencies: {}",
                            userPermissions.allowedHotels.size(), userPermissions.allowedAgencies.size());

                    // 3. Load master data in parallel (one query per type)
                    Map<String, ManageHotelDto> hotels = loadHotelsInBulk(codes.hotelCodes, userPermissions.allowedHotels);
                    Map<String, ManageAgencyDto> agencies = loadAgenciesInBulk(codes.agencyCodes, userPermissions.allowedAgencies);

                    // 3.1 Load the parent agencies of those whose aliases are not 000-MySelf or whose codes are already in codes.agencyCodes
                    Set<String> agencyParentCodes = agencies.values().stream()
                            .map(ManageAgencyDto::getAgencyAlias)
                            .filter(alias -> alias != null && !alias.trim().startsWith("000-"))
                            .map(alias -> alias.split("-", 2)[0].trim())
                            .filter(code -> !codes.agencyCodes.contains(code))
                            .collect(Collectors.toSet());
                    Map<String, ManageAgencyDto> agenciesParent = loadAgenciesInBulk(agencyParentCodes, userPermissions.allowedAgencies);
                    // Merge agenciesParent into main agencies map
                    agencies.putAll(agenciesParent);

                    // 4. Load optional data only if present in room rates
                    Map<String, ManageRoomTypeDto> roomTypes = loadRoomTypesInBulk(codes.roomTypeCodes);
                    Map<String, ManageRatePlanDto> ratePlans = loadRatePlansInBulk(codes.ratePlanCodes);
                    Map<String, ManageNightTypeDto> nightTypes = loadNightTypesInBulk(codes.nightTypeCodes);

                    // 5. Load closing operations for relevant hotels
                    Set<UUID> hotelIds = hotels.values().stream().map(ManageHotelDto::getId).collect(Collectors.toSet());
                    Map<String, InvoiceCloseOperationDto> closeOperations = loadCloseOperationsInBulk(hotelIds);

                    // 6. Load duplicates ONLY based on import type using pre-extracted combinations
                    Set<String> existingBookingNumbers = loadExistingBookingNumbers(codes.hotelBookingCombinations, importType);
                    Set<String> existingHotelInvoiceNumbers = loadExistingHotelInvoiceNumbers(codes.hotelInvoiceCombinations, importType, hotels);

                    // 6. Load invoice status and invoice Type
                    ManageInvoiceStatusDto processedStatus = invoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.PROCESSED);
                    ManageInvoiceTypeDto invoiceType = invoiceTypeService.findByEInvoiceType(EInvoiceType.INVOICE);

                    long endTime = System.currentTimeMillis();
                    long loadingTime = endTime - startTime;

                    // 7. Build full cache
                    ReferenceDataCache cache = ReferenceDataCache.builder()
                            .hotels(hotels)
                            .agencies(agencies)
                            .roomTypes(roomTypes)
                            .ratePlans(ratePlans)
                            .nightTypes(nightTypes)
                            .userAllowedHotels(userPermissions.allowedHotels)
                            .userAllowedAgencies(userPermissions.allowedAgencies)
                            .closeOperations(closeOperations)
                            .existingBookingNumbers(existingBookingNumbers)
                            .existingHotelInvoiceNumbers(existingHotelInvoiceNumbers)
                            .processedStatus(processedStatus)
                            .invoiceType(invoiceType)
                            .employeeId(employeeId)
                            .createdAt(new Date())
                            .creationTimeMs(loadingTime)
                            .build();

                    log.info("Bulk data loading completed in {}ms. Cache stats: {}",
                            loadingTime, cache.getStats());

                    return cache;
                })
                .subscribeOn(Schedulers.fromExecutor(ioExecutor))
                .doOnError(error -> log.error("Error during bulk data loading", error));
    }

    /**
     * Extracts all unique codes from room rates for bulk queries
     * FIXED: Extracts Hotel+BookingNumber/InvoiceNumber combinations directly
     * @param unifiedDtos
     * @return
     */
    private DataCodes extractUniqueCodes(List<UnifiedRoomRateDto> unifiedDtos) {
        DataCodes codes = new DataCodes();

        for (UnifiedRoomRateDto unifiedDto : unifiedDtos) {
            // Mandatory codes
            if (unifiedDto.getHotelCode() != null) {
                codes.hotelCodes.add(unifiedDto.getHotelCode().trim());
            }
            if (unifiedDto.getAgencyCode() != null) {
                codes.agencyCodes.add(unifiedDto.getAgencyCode().trim());
            }

            // Optional codes - only add if present
            if (isNotEmpty(unifiedDto.getRoomType())) {
                codes.roomTypeCodes.add(unifiedDto.getRoomType().trim());
            }
            if (isNotEmpty(unifiedDto.getRatePlan())) {
                codes.ratePlanCodes.add(unifiedDto.getRatePlan().trim());
            }
            if (isNotEmpty(unifiedDto.getNightType())) {
                codes.nightTypeCodes.add(unifiedDto.getNightType().trim());
            }

            if (isNotEmpty(unifiedDto.getHotelCode()) && isNotEmpty(unifiedDto.getHotelBookingNumber())) {
                codes.hotelBookingCombinations.add(new HotelBookingCombinationDto(
                        unifiedDto.getHotelCode().toUpperCase().trim(),
                        unifiedDto.getHotelBookingNumber().replaceAll("\\s+", " ").trim()
                ));
            }

            if (isNotEmpty(unifiedDto.getHotelCode()) && isNotEmpty(unifiedDto.getHotelInvoiceNumber())) {
                codes.hotelInvoiceCombinations.add(new HotelInvoiceCombinationDto(
                        unifiedDto.getHotelCode().toUpperCase().trim(),
                        unifiedDto.getHotelInvoiceNumber().trim()
                ));
            }
        }

        log.debug("Extracted combinations - Hotel+Booking: {}, Hotel+Invoice: {}",
                codes.hotelBookingCombinations.size(), codes.hotelInvoiceCombinations.size());

        return codes;
    }

    /**
     * Load user permissions (allowed hotels and agencies)
     * @param employeeId
     * @return
     */
    private UserPermissions loadUserPermissions(String employeeId) {
        try {
            List<UUID> hotelIds = employeeService.findHotelsIdsByEmployeeId(employeeId);
            List<UUID> agencyIds = employeeService.findAgencyIdsByEmployeeId(employeeId);

            return new UserPermissions(
                    new HashSet<>(hotelIds),
                    new HashSet<>(agencyIds)
            );
        } catch (Exception e) {
            log.error("Error loading user permissions for employee: {}", employeeId, e);
            return new UserPermissions(new HashSet<>(), new HashSet<>());
        }
    }

    /**
     * Load hotels in bulk
     * @param hotelCodes
     * @param allowedHotels
     * @return
     */
    private Map<String, ManageHotelDto> loadHotelsInBulk(Set<String> hotelCodes, Set<UUID> allowedHotels) {
        if (hotelCodes.isEmpty()) {
            return new HashMap<>();
        }

        try {
            List<ManageHotelDto> hotels = hotelService.findByCodeIn(new ArrayList<>(hotelCodes));

            // Filter only hotels to which the user has access
            return hotels.stream()
                    .filter(hotel -> allowedHotels.contains(hotel.getId()))
                    .collect(Collectors.toMap(
                            ManageHotelDto::getCode,
                            hotel -> hotel,
                            (existing, replacement) -> existing // In case of duplicates, keep the existing one
                    ));
        } catch (Exception e) {
            log.error("Error loading hotels in bulk: {}", hotelCodes, e);
            return new HashMap<>();
        }
    }

    /**
     * Load agencies in bulk
     * @param agencyCodes
     * @param allowedAgencies
     * @return
     */
    private Map<String, ManageAgencyDto> loadAgenciesInBulk(Set<String> agencyCodes, Set<UUID> allowedAgencies) {
        if (agencyCodes.isEmpty()) {
            return new HashMap<>();
        }

        try {
            List<ManageAgencyDto> agencies = agencyService.findByCodeIn(new ArrayList<>(agencyCodes));

            // Filter only agencies to which the user has access
            return agencies.stream()
                    .filter(agency -> allowedAgencies.contains(agency.getId()))
                    .collect(Collectors.toMap(
                            ManageAgencyDto::getCode,
                            agency -> agency,
                            (existing, replacement) -> existing
                    ));
        } catch (Exception e) {
            log.error("Error loading agencies in bulk: {}", agencyCodes, e);
            return new HashMap<>();
        }
    }

    /**
     * Load room types in bulk - only if codes are present
     * @param roomTypeCodes
     * @return
     */
    private Map<String, ManageRoomTypeDto> loadRoomTypesInBulk(Set<String> roomTypeCodes) {
        if (roomTypeCodes.isEmpty()) {
            return new HashMap<>();
        }

        try {
            List<ManageRoomTypeDto> roomTypes = roomTypeService.findByCodes(new ArrayList<>(roomTypeCodes));
            return roomTypes.stream()
                    .collect(Collectors.toMap(
                            ManageRoomTypeDto::getCode,
                            roomType -> roomType,
                            (existing, replacement) -> existing
                    ));
        } catch (Exception e) {
            log.error("Error loading room types in bulk: {}", roomTypeCodes, e);
            return new HashMap<>();
        }
    }

    /**
     * Load rate plans in bulk - only if codes are present
     * @param ratePlanCodes
     * @return
     */
    private Map<String, ManageRatePlanDto> loadRatePlansInBulk(Set<String> ratePlanCodes) {
        if (ratePlanCodes.isEmpty()) {
            return new HashMap<>();
        }

        try {
            List<ManageRatePlanDto> ratePlans = ratePlanService.findByCodes(new ArrayList<>(ratePlanCodes));
            return ratePlans.stream()
                    .collect(Collectors.toMap(
                            ManageRatePlanDto::getCode,
                            ratePlan -> ratePlan,
                            (existing, replacement) -> existing
                    ));
        } catch (Exception e) {
            log.error("Error loading rate plans in bulk: {}", ratePlanCodes, e);
            return new HashMap<>();
        }
    }

    /**
     * Load night types in bulk - only if codes are present
     * @param nightTypeCodes
     * @return
     */
    private Map<String, ManageNightTypeDto> loadNightTypesInBulk(Set<String> nightTypeCodes) {
        if (nightTypeCodes.isEmpty()) {
            return new HashMap<>();
        }

        try {
            List<ManageNightTypeDto> nightTypes = nightTypeService.findByCodes(new ArrayList<>(nightTypeCodes));
            return nightTypes.stream()
                    .collect(Collectors.toMap(
                            ManageNightTypeDto::getCode,
                            nightType -> nightType,
                            (existing, replacement) -> existing
                    ));
        } catch (Exception e) {
            log.error("Error loading night types in bulk: {}", nightTypeCodes, e);
            return new HashMap<>();
        }
    }

    /**
     * Load closing operations for relevant hotels
     * @param hotelIds
     * @return
     */
    private Map<String, InvoiceCloseOperationDto> loadCloseOperationsInBulk(Set<UUID> hotelIds) {
        if (hotelIds.isEmpty()) {
            return new HashMap<>();
        }

        try {
            List<InvoiceCloseOperationDto> closeOps = closeOperationService.findByHotelIds(new ArrayList<>(hotelIds));
            return closeOps.stream()
                    .collect(Collectors.toMap(
                            closeOp -> closeOp.getHotel().getCode(),
                            closeOp -> closeOp,
                            (existing, replacement) -> existing
                    ));
        } catch (Exception e) {
            log.error("Error loading close operations in bulk: {}", hotelIds, e);
            return new HashMap<>();
        }
    }

    /**
     * Load existing booking numbers - OPTIMIZED and FIXED
     * Use pre-extracted combinations to avoid duplicate logic
     * @param combinations
     * @param importType
     * @return
     */
    private Set<String> loadExistingBookingNumbers(Set<HotelBookingCombinationDto> combinations, ImportType importType) {

        if (combinations.isEmpty()) {
            log.debug("No booking number combinations to check for importType: {}", importType);
            return new HashSet<>();
        }

        // Filter additional combinations by import type if needed
        List<HotelBookingCombinationDto> filteredCombinations = combinations.stream()
                .filter(combination -> shouldValidateBookingForImportType(combination, importType))
                .collect(Collectors.toList());

        if (filteredCombinations.isEmpty()) {
            log.debug("No booking combinations to validate after filtering for importType: {}", importType);
            return new HashSet<>();
        }

        try {
            DuplicateValidationResult result = bookingService.validateHotelBookingCombinations(filteredCombinations, importType);

            log.info("Validation result for {}: checked {}, found {} duplicates", importType, result.getTotalChecked(), result.getDuplicatesFound());

            return result.getExistingCombinationKeys();

        } catch (Exception e) {
            log.error("Error loading existing booking numbers for importType: {}", importType, e);
            return new HashSet<>();
        }
    }

    /**
     * Upload hotel invoice numbers - OPTIMIZED and FIXED
     * Use pre-extracted combinations and filter by virtual hotels
     * @param combinations
     * @param importType
     * @param hotels
     * @return
     */
    private Set<String> loadExistingHotelInvoiceNumbers(Set<HotelInvoiceCombinationDto> combinations, ImportType importType,
                                                        Map<String, ManageHotelDto> hotels) {

         if (importType == ImportType.BOOKING_FROM_FILE_VIRTUAL_HOTEL) {
            log.debug("Skipping hotel invoice number validation for non-virtual import: {}", importType);
            return new HashSet<>();
        }

        if (combinations.isEmpty()) {
            log.debug("No hotel invoice combinations to check");
            return new HashSet<>();
        }

        // Filter only virtual hotel combinations
        List<HotelInvoiceCombinationDto> virtualHotelCombinations = combinations.stream()
                .filter(combination -> {
                    ManageHotelDto hotel = hotels.get(combination.getHotelCode());
                    return hotel != null && hotel.isVirtual();
                })
                .collect(Collectors.toList());

        if (virtualHotelCombinations.isEmpty()) {
            log.debug("No virtual hotel invoice combinations to check");
            return new HashSet<>();
        }

        try {
            DuplicateValidationResult result = bookingService.validateHotelInvoiceCombinations(virtualHotelCombinations, importType);

            log.info("Virtual hotel validation result: checked {}, found {} duplicates",
                    result.getTotalChecked(), result.getDuplicatesFound());

            return result.getExistingCombinationKeys();

        } catch (Exception e) {
            log.error("Error loading existing hotel invoice numbers for virtual hotels", e);
            return new HashSet<>();
        }
    }

    /**
     * Determines whether to validate duplicate bookings based on the import type
     * @param combination
     * @param importType
     * @return
     */
    private boolean shouldValidateBookingForImportType(HotelBookingCombinationDto combination, ImportType importType) {

        switch (importType) {
            case INVOICE_BOOKING_FROM_FILE:
            case BOOKING_FROM_FILE_VIRTUAL_HOTEL:
            case INSIST:
                return true;
            case NONE:
            default:
                log.warn("Unknown import type: {}, defaulting to validate", importType);
                return true;
        }
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // Helper classes for organizing data
    private static class DataCodes {
        Set<String> hotelCodes = new HashSet<>();
        Set<String> agencyCodes = new HashSet<>();
        Set<String> roomTypeCodes = new HashSet<>();
        Set<String> ratePlanCodes = new HashSet<>();
        Set<String> nightTypeCodes = new HashSet<>();
        Set<HotelBookingCombinationDto> hotelBookingCombinations = new HashSet<>();
        Set<HotelInvoiceCombinationDto> hotelInvoiceCombinations = new HashSet<>();
    }

    private static class UserPermissions {
        Set<UUID> allowedHotels;
        Set<UUID> allowedAgencies;

        UserPermissions(Set<UUID> allowedHotels, Set<UUID> allowedAgencies) {
            this.allowedHotels = allowedHotels;
            this.allowedAgencies = allowedAgencies;
        }
    }
}