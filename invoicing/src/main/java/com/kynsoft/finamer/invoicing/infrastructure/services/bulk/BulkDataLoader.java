package com.kynsoft.finamer.invoicing.infrastructure.services.bulk;

import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dto.cache.ReferenceDataCache;
import com.kynsoft.finamer.invoicing.domain.dto.roomrate.UnifiedRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.dto.validation.DuplicateValidationResult;
import com.kynsoft.finamer.invoicing.domain.dto.validation.HotelBookingCombinationDto;
import com.kynsoft.finamer.invoicing.domain.dto.validation.HotelInvoiceCombinationDto;
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
 * Servicio optimizado para cargar en masa todos los datos de referencia necesarios
 * para validar y procesar room rates. Elimina consultas N+1 y mejora performance.
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
    private final IManageBookingService bookingService; // Para cargar booking numbers existentes

    // Inyectar el executor de I/O directamente
    @Qualifier("ioExecutor")
    private final TaskExecutor ioExecutor;

    /**
     * Precarga todos los datos de referencia necesarios para procesar una lista de room rates
     *
     * @param roomRates Lista de room rates a procesar
     * @param employeeId ID del empleado que ejecuta la importación
     * @param importType Tipo de importación (VIRTUAL, NO_VIRTUAL, INNSIST)
     * @return Cache completo con todos los datos necesarios
     */
    public Mono<ReferenceDataCache> preloadReferenceData(List<UnifiedRoomRateDto> roomRates, String employeeId, String importType) {
        log.info("Starting bulk data loading for {} room rates, employee: {}, importType: {}",
                roomRates.size(), employeeId, importType);
        long startTime = System.currentTimeMillis();

        return Mono.fromCallable(() -> {

                    // 1. Extraer todos los códigos únicos del Excel/datos de entrada
                    DataCodes codes = extractUniqueCodes(roomRates);
                    log.debug("Extracted codes - Hotels: {}, Agencies: {}, RoomTypes: {}, RatePlans: {}, NightTypes: {}",
                            codes.hotelCodes.size(), codes.agencyCodes.size(),
                            codes.roomTypeCodes.size(), codes.ratePlanCodes.size(), codes.nightTypeCodes.size());

                    // 2. Cargar permisos del usuario primero (necesario para filtrar datos)
                    UserPermissions userPermissions = loadUserPermissions(employeeId);
                    log.debug("User permissions loaded - Hotels: {}, Agencies: {}",
                            userPermissions.allowedHotels.size(), userPermissions.allowedAgencies.size());

                    // 3. Cargar datos maestros en paralelo (una consulta por tipo)
                    Map<String, ManageHotelDto> hotels = loadHotelsInBulk(codes.hotelCodes, userPermissions.allowedHotels);
                    Map<String, ManageAgencyDto> agencies = loadAgenciesInBulk(codes.agencyCodes, userPermissions.allowedAgencies);

                    // 4. Cargar datos opcionales solo si están presentes en los room rates
                    Map<String, ManageRoomTypeDto> roomTypes = loadRoomTypesInBulk(codes.roomTypeCodes);
                    Map<String, ManageRatePlanDto> ratePlans = loadRatePlansInBulk(codes.ratePlanCodes);
                    Map<String, ManageNightTypeDto> nightTypes = loadNightTypesInBulk(codes.nightTypeCodes);

                    // 5. Cargar operaciones de cierre para hoteles relevantes
                    Set<UUID> hotelIds = hotels.values().stream().map(ManageHotelDto::getId).collect(Collectors.toSet());
                    Map<String, InvoiceCloseOperationDto> closeOperations = loadCloseOperationsInBulk(hotelIds);

                    // 6. Cargar duplicados SOLO según el tipo de importación usando combinaciones pre-extraídas
                    Set<String> existingBookingNumbers = loadExistingBookingNumbers(codes.hotelBookingCombinations, importType);
                    Set<String> existingHotelInvoiceNumbers = loadExistingHotelInvoiceNumbers(codes.hotelInvoiceCombinations, importType, hotels);

                    long endTime = System.currentTimeMillis();
                    long loadingTime = endTime - startTime;

                    // 7. Construir cache completo
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
     * Extrae todos los códigos únicos de los room rates para hacer consultas bulk
     * CORREGIDO: Extrae combinaciones Hotel+BookingNumber/InvoiceNumber directamente
     */
    private DataCodes extractUniqueCodes(List<UnifiedRoomRateDto> roomRates) {
        DataCodes codes = new DataCodes();

        for (UnifiedRoomRateDto roomRate : roomRates) {
            // Códigos obligatorios
            if (roomRate.getHotelCode() != null) {
                codes.hotelCodes.add(roomRate.getHotelCode().trim());
            }
            if (roomRate.getAgencyCode() != null) {
                codes.agencyCodes.add(roomRate.getAgencyCode().trim());
            }

            // Códigos opcionales - solo agregar si están presentes
            if (isNotEmpty(roomRate.getRoomType())) {
                codes.roomTypeCodes.add(roomRate.getRoomType().trim());
            }
            if (isNotEmpty(roomRate.getRatePlan())) {
                codes.ratePlanCodes.add(roomRate.getRatePlan().trim());
            }
            if (isNotEmpty(roomRate.getNightType())) {
                codes.nightTypeCodes.add(roomRate.getNightType().trim());
            }

            if (isNotEmpty(roomRate.getHotelCode()) && isNotEmpty(roomRate.getHotelBookingNumber())) {
                codes.hotelBookingCombinations.add(new HotelBookingCombinationDto(
                        roomRate.getHotelCode().toUpperCase().trim(),
                        roomRate.getHotelBookingNumber().replaceAll("\\s+", " ").trim()
                ));
            }

            if (isNotEmpty(roomRate.getHotelCode()) && isNotEmpty(roomRate.getHotelInvoiceNumber())) {
                codes.hotelInvoiceCombinations.add(new HotelInvoiceCombinationDto(
                        roomRate.getHotelCode().toUpperCase().trim(),
                        roomRate.getHotelInvoiceNumber().trim()
                ));
            }
        }

        log.debug("Extracted combinations - Hotel+Booking: {}, Hotel+Invoice: {}",
                codes.hotelBookingCombinations.size(), codes.hotelInvoiceCombinations.size());

        return codes;
    }

    /**
     * Carga permisos del usuario (hoteles y agencias permitidas)
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
     * Carga hoteles en masa - UNA SOLA consulta
     */
    private Map<String, ManageHotelDto> loadHotelsInBulk(Set<String> hotelCodes, Set<UUID> allowedHotels) {
        if (hotelCodes.isEmpty()) {
            return new HashMap<>();
        }

        try {
            List<ManageHotelDto> hotels = hotelService.findByCodeIn(new ArrayList<>(hotelCodes));

            // Filtrar solo hoteles a los que el usuario tiene acceso
            return hotels.stream()
                    .filter(hotel -> allowedHotels.contains(hotel.getId()))
                    .collect(Collectors.toMap(
                            ManageHotelDto::getCode,
                            hotel -> hotel,
                            (existing, replacement) -> existing // En caso de duplicados, mantener el existente
                    ));
        } catch (Exception e) {
            log.error("Error loading hotels in bulk: {}", hotelCodes, e);
            return new HashMap<>();
        }
    }

    /**
     * Carga agencias en masa - UNA SOLA consulta
     */
    private Map<String, ManageAgencyDto> loadAgenciesInBulk(Set<String> agencyCodes, Set<UUID> allowedAgencies) {
        if (agencyCodes.isEmpty()) {
            return new HashMap<>();
        }

        try {
            List<ManageAgencyDto> agencies = agencyService.findByCodeIn(new ArrayList<>(agencyCodes));

            // Filtrar solo agencias a las que el usuario tiene acceso
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
     * Carga room types en masa - solo si hay códigos presentes
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
     * Carga rate plans en masa - solo si hay códigos presentes
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
     * Carga night types en masa - solo si hay códigos presentes
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
     * Carga operaciones de cierre para los hoteles relevantes
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
     * Carga booking numbers existentes - OPTIMIZADO y CORREGIDO
     * Usa combinaciones pre-extraídas evitando duplicar lógica
     */
    private Set<String> loadExistingBookingNumbers(Set<HotelBookingCombinationDto> combinations, String importType) {

        if (combinations.isEmpty()) {
            log.debug("No booking number combinations to check for importType: {}", importType);
            return new HashSet<>();
        }

        // Filtrar combinaciones adicionales según import type si es necesario
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
     * Carga hotel invoice numbers - OPTIMIZADO y CORREGIDO
     * Usa combinaciones pre-extraídas y filtra por hoteles virtuales
     */
    private Set<String> loadExistingHotelInvoiceNumbers(Set<HotelInvoiceCombinationDto> combinations, String importType, Map<String,
            ManageHotelDto> hotels) {

         if (!"VIRTUAL".equals(importType)) {
            log.debug("Skipping hotel invoice number validation for non-virtual import: {}", importType);
            return new HashSet<>();
        }

        if (combinations.isEmpty()) {
            log.debug("No hotel invoice combinations to check");
            return new HashSet<>();
        }

        // Filtrar solo combinaciones de hoteles virtuales
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
     * Determina si debe validar booking duplicados según el tipo de importación
     */
    private boolean shouldValidateBookingForImportType(HotelBookingCombinationDto combination, String importType) {
        // Todas las importaciones necesitan validar booking numbers
        // Esta lógica se puede expandir si hay casos específicos por tipo

        switch (importType) {
            case "VIRTUAL":
            case "NO_VIRTUAL":
            case "INNSIST":
                return true;
            default:
                log.warn("Unknown import type: {}, defaulting to validate", importType);
                return true;
        }
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // Clases auxiliares para organizar datos
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