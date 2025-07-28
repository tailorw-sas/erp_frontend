package com.kynsoft.finamer.invoicing.domain.dto.cache;

import com.kynsoft.finamer.invoicing.domain.dto.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;

/**
 * Cache en memoria de todos los datos de referencia necesarios para la validación
 * y procesamiento de Room Rates. Elimina la necesidad de consultas repetitivas a BD.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceDataCache {

    // === Datos Maestros ===
    private Map<String, ManageHotelDto> hotels;
    private Map<String, ManageAgencyDto> agencies;
    private Map<String, ManageRoomTypeDto> roomTypes;
    private Map<String, ManageRatePlanDto> ratePlans;
    private Map<String, ManageNightTypeDto> nightTypes;

    // === Permisos del Usuario ===
    private Set<UUID> userAllowedHotels;
    private Set<UUID> userAllowedAgencies;

    // === Operaciones de Cierre ===
    private Map<String, InvoiceCloseOperationDto> closeOperations; // Por código de hotel

    // === Datos para Validaciones Avanzadas ===
    private Set<String> existingBookingNumbers; // Hotel booking numbers ya existentes en BD
    private Set<String> existingHotelInvoiceNumbers; // Hotel invoice numbers ya existentes en BD

    // === Metadatos del Cache ===
    private String employeeId;
    private Date createdAt;
    private long creationTimeMs;

    /**
     * Verifica si un hotel existe y el usuario tiene acceso
     */
    public boolean hasValidHotelAccess(String hotelCode) {
        ManageHotelDto hotel = hotels.get(hotelCode);
        return hotel != null && userAllowedHotels.contains(hotel.getId());
    }

    /**
     * Verifica si una agencia existe y el usuario tiene acceso
     */
    public boolean hasValidAgencyAccess(String agencyCode) {
        ManageAgencyDto agency = agencies.get(agencyCode);
        return agency != null && userAllowedAgencies.contains(agency.getId());
    }

    /**
     * Obtiene un hotel por código si existe y hay acceso
     */
    public Optional<ManageHotelDto> getHotelIfAllowed(String hotelCode) {
        ManageHotelDto hotel = hotels.get(hotelCode);
        if (hotel != null && userAllowedHotels.contains(hotel.getId())) {
            return Optional.of(hotel);
        }
        return Optional.empty();
    }

    /**
     * Obtiene una agencia por código si existe y hay acceso
     */
    public Optional<ManageAgencyDto> getAgencyIfAllowed(String agencyCode) {
        ManageAgencyDto agency = agencies.get(agencyCode);
        if (agency != null && userAllowedAgencies.contains(agency.getId())) {
            return Optional.of(agency);
        }
        return Optional.empty();
    }

    /**
     * Verifica si un room type existe para un hotel específico
     */
    public boolean hasValidRoomType(String roomTypeCode, String hotelCode) {
        if (roomTypeCode == null || roomTypeCode.trim().isEmpty()) {
            return true; // Room type puede ser opcional
        }
        return roomTypes.containsKey(roomTypeCode);
    }

    /**
     * Verifica si un rate plan existe para un hotel específico
     */
    public boolean hasValidRatePlan(String ratePlanCode, String hotelCode) {
        if (ratePlanCode == null || ratePlanCode.trim().isEmpty()) {
            return true; // Rate plan puede ser opcional
        }
        return ratePlans.containsKey(ratePlanCode);
    }

    /**
     * Verifica si un night type existe
     */
    public boolean hasValidNightType(String nightTypeCode) {
        if (nightTypeCode == null || nightTypeCode.trim().isEmpty()) {
            return true; // Night type puede ser opcional
        }
        return nightTypes.containsKey(nightTypeCode);
    }

    /**
     * Verifica si hay operaciones de cierre que impidan procesar en una fecha específica
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
     * Verifica si un hotel booking number ya existe en BD
     */
    public boolean isBookingNumberDuplicate(String hotelBookingNumber) {
        if (hotelBookingNumber == null) return false;
        String cleanNumber = hotelBookingNumber.replaceAll("\\s+", " ").trim();
        return existingBookingNumbers.contains(cleanNumber);
    }

    /**
     * Verifica si un hotel invoice number ya existe en BD
     */
    public boolean isHotelInvoiceNumberDuplicate(String hotelInvoiceNumber) {
        if (hotelInvoiceNumber == null) return false;
        return existingHotelInvoiceNumbers.contains(hotelInvoiceNumber);
    }

    /**
     * Obtiene estadísticas del cache para monitoreo
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
     * Valida que el cache tenga todos los datos esenciales
     */
    public boolean isValid() {
        return hotels != null && !hotels.isEmpty() &&
                agencies != null && !agencies.isEmpty() &&
                userAllowedHotels != null &&
                userAllowedAgencies != null &&
                employeeId != null;
    }

    /**
     * Factory method para crear un cache vacío
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

        public double getCreationTimeSeconds() {
            return creationTimeMs / 1000.0;
        }

        public double getCacheAgeSeconds() {
            return cacheAgeMs / 1000.0;
        }
    }
}
