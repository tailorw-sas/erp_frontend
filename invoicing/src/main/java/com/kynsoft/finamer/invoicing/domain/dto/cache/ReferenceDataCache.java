package com.kynsoft.finamer.invoicing.domain.dto.cache;

import com.kynsoft.finamer.invoicing.domain.dto.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

/**
 * In-memory cache containing all reference data required for Room Rate validation and processing.
 * Eliminates the need for repetitive database lookups.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceDataCache {

    // === Master Data ===
    private Map<String, ManageHotelDto> hotels;
    private Map<String, ManageAgencyDto> agencies;
    private Map<String, ManageRoomTypeDto> roomTypes;
    private Map<String, ManageRatePlanDto> ratePlans;
    private Map<String, ManageNightTypeDto> nightTypes;
    private ManageInvoiceStatusDto processedStatus;
    private ManageInvoiceTypeDto invoiceType;

    // === User Permissions ===
    private Set<UUID> userAllowedHotels;
    private Set<UUID> userAllowedAgencies;

    // === Close Operations ===
    private Map<String, InvoiceCloseOperationDto> closeOperations; // By hotel code

    // === Advanced Validation Data ===
    private Set<String> existingBookingNumbers; // Existing hotel booking numbers in DB
    private Set<String> existingHotelInvoiceNumbers; // Existing hotel invoice numbers in DB

    // === Cache Metadata ===
    private String employeeId;
    private Date createdAt;
    private long creationTimeMs;

    /**
     * Checks whether the hotel exists and the user has access to it.
     *
     * @param hotelCode Hotel code to validate
     * @return true if the hotel exists and the user is authorized, false otherwise
     */
    public boolean hasValidHotelAccess(String hotelCode) {
        ManageHotelDto hotel = hotels.get(hotelCode);
        return hotel != null && userAllowedHotels.contains(hotel.getId());
    }

    /**
     * Checks whether the agency exists and the user has access to it.
     *
     * @param agencyCode Agency code to validate
     * @return true if the agency exists and the user is authorized, false otherwise
     */
    public boolean hasValidAgencyAccess(String agencyCode) {
        ManageAgencyDto agency = agencies.get(agencyCode);
        return agency != null && userAllowedAgencies.contains(agency.getId());
    }

    /**
     * Retrieves the hotel if it exists and the user has access to it.
     *
     * @param hotelCode Hotel code
     * @return Optional of ManageHotelDto if valid and accessible
     */
    public Optional<ManageHotelDto> getHotelIfAllowed(String hotelCode) {
        ManageHotelDto hotel = hotels.get(hotelCode);
        if (hotel != null && userAllowedHotels.contains(hotel.getId())) {
            return Optional.of(hotel);
        }
        return Optional.empty();
    }

    /**
     * Retrieves the agency if it exists and the user has access to it.
     *
     * @param agencyCode Agency code
     * @return Optional of ManageAgencyDto if valid and accessible
     */
    public Optional<ManageAgencyDto> getAgencyIfAllowed(String agencyCode) {
        ManageAgencyDto agency = agencies.get(agencyCode);
        if (agency != null && userAllowedAgencies.contains(agency.getId())) {
            return Optional.of(agency);
        }
        return Optional.empty();
    }

    /**
     * Validates whether a room type exists (room types can be optional).
     *
     * @param roomTypeCode Room type code
     * @param hotelCode Hotel code (unused currently)
     * @return true if valid or empty, false otherwise
     */
    public boolean hasValidRoomType(String roomTypeCode, String hotelCode) {
        if (roomTypeCode == null || roomTypeCode.trim().isEmpty()) {
            return true; // Room type puede ser opcional
        }
        return roomTypes.containsKey(roomTypeCode);
    }

    /**
     * Validates whether a rate plan exists (rate plans can be optional).
     *
     * @param ratePlanCode Rate plan code
     * @param hotelCode Hotel code (unused currently)
     * @return true if valid or empty, false otherwise
     */
    public boolean hasValidRatePlan(String ratePlanCode, String hotelCode) {
        if (ratePlanCode == null || ratePlanCode.trim().isEmpty()) {
            return true; // Rate plan puede ser opcional
        }
        return ratePlans.containsKey(ratePlanCode);
    }

    /**
     * Validates whether a night type exists (can be optional).
     *
     * @param nightTypeCode Night type code
     * @return true if valid or empty, false otherwise
     */
    public boolean hasValidNightType(String nightTypeCode) {
        if (nightTypeCode == null || nightTypeCode.trim().isEmpty()) {
            return true; // Night type puede ser opcional
        }
        return nightTypes.containsKey(nightTypeCode);
    }

    /**
     * Checks whether the given transaction date is within the allowed window
     * for a hotel based on close operation definitions.
     *
     * @param hotelCode Hotel code
     * @param transactionDate Date in string format to validate
     * @return true if allowed to process, false otherwise
     */
    public boolean isDateAllowedForProcessing(String hotelCode, String transactionDate) {
        InvoiceCloseOperationDto closeOp = closeOperations.get(hotelCode);
        if (closeOp == null) {
            return true; // No hay restricciones si no existe operación de cierre
        }

        try {
            LocalDate transDate = com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil.parseDateToLocalDate(transactionDate);
            LocalDate beginDate = closeOp.getBeginDate();
            LocalDate endDate = closeOp.getEndDate();

            // La fecha debe estar dentro del rango permitido
            return !transDate.isBefore(beginDate) && !transDate.isAfter(endDate);

        } catch (Exception e) {
            return false; // Si no se puede parsear la fecha, no permitir
        }
    }

    /**
     * Checks whether a hotel booking number already exists in the system.
     *
     * @param hotelBookingNumber Booking number to check
     * @return true if duplicate, false otherwise
     */
    public boolean isBookingNumberDuplicate(String hotelBookingNumber) {
        if (hotelBookingNumber == null) return false;
        String cleanNumber = hotelBookingNumber.replaceAll("\\s+", " ").trim();
        return existingBookingNumbers.contains(cleanNumber);
    }

    /**
     * Checks whether a hotel invoice number already exists in the system.
     *
     * @param hotelInvoiceNumber Invoice number to check
     * @return true if duplicate, false otherwise
     */
    public boolean isHotelInvoiceNumberDuplicate(String hotelInvoiceNumber) {
        if (hotelInvoiceNumber == null) return false;
        return existingHotelInvoiceNumbers.contains(hotelInvoiceNumber);
    }

    /**
     * Returns statistics about the cached data for monitoring purposes.
     *
     * @return CacheStats with aggregated information
     */
    public CacheStats getStats() {
        return CacheStats.builder()
                .hotelCount(hotels.size())
                .agencyCount(agencies.size())
                .roomTypeCount(roomTypes.size())
                .ratePlanCount(ratePlans.size())
                .nightTypeCount(nightTypes.size())
                .userHotelCount(userAllowedHotels.size())
                .userAgencyCount(userAllowedAgencies.size())
                .creationTimeMs(creationTimeMs)
                .cacheAgeMs(System.currentTimeMillis() - createdAt.getTime())
                .build();
    }

    /**
     * Validates that the cache contains all required reference data to be considered valid.
     *
     * @return true if cache is usable, false otherwise
     */
    public boolean isValid() {
        return hotels != null && !hotels.isEmpty() &&
                agencies != null && !agencies.isEmpty() &&
                userAllowedHotels != null &&
                userAllowedAgencies != null &&
                employeeId != null;
    }

    /**
     * Factory method to create an empty cache for a given employee.
     *
     * @param employeeId ID of the employee initiating the cache
     * @return ReferenceDataCache with initialized empty maps and sets
     */
    public static ReferenceDataCache empty(String employeeId) {
        return ReferenceDataCache.builder()
                .hotels(new HashMap<>())
                .agencies(new HashMap<>())
                .roomTypes(new HashMap<>())
                .ratePlans(new HashMap<>())
                .nightTypes(new HashMap<>())
                .userAllowedHotels(new HashSet<>())
                .userAllowedAgencies(new HashSet<>())
                .closeOperations(new HashMap<>())
                .existingBookingNumbers(new HashSet<>())
                .existingHotelInvoiceNumbers(new HashSet<>())
                .employeeId(employeeId)
                .createdAt(new Date())
                .build();
    }

    @Data
    @Builder
    /**
     * Snapshot of cache statistics used for observability and diagnostics.
     */
    public static class CacheStats {
        private int hotelCount;
        private int agencyCount;
        private int roomTypeCount;
        private int ratePlanCount;
        private int nightTypeCount;
        private int userHotelCount;
        private int userAgencyCount;
        private long creationTimeMs;
        private long cacheAgeMs;

        /**
         * @return creation time in seconds
         */
        public double getCreationTimeSeconds() {
            return creationTimeMs / 1000.0;
        }

        /**
         * @return age of the cache in seconds
         */
        public double getCacheAgeSeconds() {
            return cacheAgeMs / 1000.0;
        }
    }
}
