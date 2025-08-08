package com.kynsoft.finamer.invoicing.infrastructure.services.orchestrator;

import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.importresult.ImportResult;
import com.kynsoft.finamer.invoicing.domain.dto.roomrate.UnifiedRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.dto.validation.ValidationResult;
import com.kynsoft.finamer.invoicing.domain.dto.cache.ReferenceDataCache;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EGenerationType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.invoicing.domain.interfaces.RoomRateSourceAdapter;
import com.kynsoft.finamer.invoicing.infrastructure.services.bulk.BulkDataLoader;
import com.kynsoft.finamer.invoicing.infrastructure.services.processing.RoomRateProcessor;
import com.kynsoft.finamer.invoicing.infrastructure.services.progress.ImportProgressService;
import com.kynsoft.finamer.invoicing.infrastructure.services.validation.BusinessRuleValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Main orchestrator that coordinates the entire Room Rates import process
 * from multiple sources (Excel, external APIs, etc.) to invoice generation.
 *
 * Handles the entire workflow: Adaptation → Validation → Processing
 */
@Service
@Slf4j
public class UnifiedRoomRateImportOrchestrator {

    private final Map<String, RoomRateSourceAdapter> adapters;
    private final BulkDataLoader bulkDataLoader;
    private final BusinessRuleValidator businessRuleValidator;
    private final RoomRateProcessor roomRateProcessor;
    private final ImportProgressService progressService;

    @Qualifier("importExecutor")
    private final TaskExecutor importExecutor;

    /**
     * Constructs the UnifiedRoomRateImportOrchestrator with all required dependencies.
     *
     * @param adapterList List of source adapters to handle different input sources (e.g., Excel, APIs)
     * @param bulkDataLoader Loader responsible for loading reference/master data
     * @param businessRuleValidator Component to apply validation rules to the room rate data
     * @param roomRateProcessor Component responsible for generating bookings and invoices
     * @param progressService Service to track the progress of the import process
     * @param importExecutor TaskExecutor used to execute imports asynchronously
     */
    public UnifiedRoomRateImportOrchestrator(
            @Autowired(required = false) List<RoomRateSourceAdapter> adapterList,
            BulkDataLoader bulkDataLoader,
            BusinessRuleValidator businessRuleValidator,
            RoomRateProcessor roomRateProcessor,
            ImportProgressService progressService,
            @Qualifier("importExecutor") TaskExecutor importExecutor) {

        this.adapters = adapterList != null ?
                adapterList.stream().collect(Collectors.toMap(
                        RoomRateSourceAdapter::getSourceType,
                        Function.identity()
                )) : new HashMap<>();

        this.bulkDataLoader = bulkDataLoader;
        this.businessRuleValidator = businessRuleValidator;
        this.roomRateProcessor = roomRateProcessor;
        this.progressService = progressService;
        this.importExecutor = importExecutor;

        log.info("Initialized orchestrator with {} adapters: {}", adapters.size(), adapters.keySet());
    }

    /**
     * Processes imports from any source asynchronously
     *
     * @param source Source data (List<BookingRow>, List<ExternalDto>, etc.)
     * @param sourceType Source type ("EXCEL", "EXTERNAL_SYSTEM_A", etc.)
     * @param importType Import type (NONE, INVOICE_BOOKING_FROM_FILE, BOOKING_FROM_FILE_VIRTUAL_HOTEL, INSIST)
     * @param importProcessId Unique process ID
     * @param employee ID of the employee performing the import
     * @return Full result of the process
     */
    @Async("importExecutor")
    public Mono<ImportResult> processImport(Object source, String sourceType, ImportType importType, String importProcessId, String employee) {
        log.info("Starting import process {} from source type: {}, importType: {}", importProcessId, sourceType, importType);
        LocalDateTime startTime = LocalDateTime.now();

        return processUnifiedImport(source, sourceType, importType, importProcessId, employee, startTime)
                .doOnSuccess(result -> {
                    log.info("Import process {} completed. Success: {}, Processed: {}/{}, Duration: {}ms",
                            importProcessId, result.isSuccess(), result.getProcessedCount(),
                            result.getTotalCount(), result.getDurationMs());
                })
                .doOnError(error -> {
                    log.error("Import process {} failed", importProcessId, error);
                    progressService.updateProgress(importProcessId, "FAILED", 0, error.getMessage());
                });
    }

    /**
     * Executes the unified import process: initialization, data adaptation, validation, and processing.
     *
     * @param source The raw input source (Excel data, external DTOs, etc.)
     * @param sourceType Identifier for the source type (e.g., "EXCEL", "API")
     * @param importType Import classification (e.g., NONE, INVOICE_BOOKING_FROM_FILE, BOOKING_FROM_FILE_VIRTUAL_HOTEL, INSIST)
     * @param importProcessId Unique identifier for the import session
     * @param employee ID of the employee initiating the import
     * @param startTime Timestamp when the import started
     * @return Mono containing the result of the import process
     */
    private Mono<ImportResult> processUnifiedImport(Object source, String sourceType, ImportType importType, String importProcessId, String employee,
                                                    LocalDateTime startTime) {

        return initializeImport(importProcessId, sourceType, startTime)
                .flatMap(result -> adaptSourceToUnified(source, sourceType, importProcessId))
                .flatMap(roomRates -> processUnifiedRoomRates(roomRates, sourceType, importType, importProcessId, employee,
                        startTime))
                .onErrorResume(error -> handleImportError(error, importProcessId, sourceType, startTime));
    }

    /**
     * Initializes the import result object and sets the initial progress.
     *
     * @param importProcessId Unique identifier for the import session
     * @param sourceType The type of data source being imported
     * @param startTime Timestamp for when the process began
     * @return Mono of an initialized ImportResult object
     */
    private Mono<ImportResult> initializeImport(String importProcessId, String sourceType, LocalDateTime startTime) {
        return Mono.fromCallable(() -> {
                    progressService.updateProgress(importProcessId, "INITIALIZING", 0, "Starting import process");

                    return ImportResult.builder()
                            .importProcessId(importProcessId)
                            .sourceType(sourceType)
                            .startTime(startTime)
                            .build();
                })
                .subscribeOn(Schedulers.fromExecutor(importExecutor));
    }

    /**
     * Converts raw input data into a list of UnifiedRoomRateDto using the appropriate adapter.
     *
     * @param source The raw source object (Excel data, external DTOs)
     * @param sourceType String identifier for the type of adapter to use
     * @param importProcessId The unique process ID for this import session
     * @return Mono containing the list of unified room rate DTOs
     */
    private Mono<List<UnifiedRoomRateDto>> adaptSourceToUnified(Object source, String sourceType, String importProcessId) {
        log.debug("Adapting source data for process: {}", importProcessId);
        progressService.updateProgress(importProcessId, "ADAPTING_DATA", 5, "Converting source data to unified format");

        RoomRateSourceAdapter adapter = adapters.get(sourceType);
        if (adapter == null) {
            return Mono.error(new IllegalArgumentException("No adapter found for source type: " + sourceType));
        }

        if (!adapter.canHandle(source)) {
            return Mono.error(new IllegalArgumentException("Adapter cannot handle provided source data"));
        }

        return adapter.adaptToUnified(source, importProcessId)
                .doOnSuccess(roomRates -> {
                    log.info("Successfully adapted {} room rates from {}", roomRates.size(), sourceType);
                    progressService.updateProgress(importProcessId, "DATA_ADAPTED", 10,
                            String.format("Adapted %d room rates", roomRates.size()));
                });
    }

    /**
     * Executes the entire validation and processing logic on the provided room rates.
     *
     * @param roomRates The unified room rate records to process
     * @param sourceType Type of the source system (e.g., "EXCEL")
     * @param importType Type of import (e.g., NONE, INVOICE_BOOKING_FROM_FILE, BOOKING_FROM_FILE_VIRTUAL_HOTEL, INSIST)
     * @param importProcessId Unique identifier for this import session
     * @param employeeId Identifier of the user performing the import
     * @param startTime Timestamp of when import started
     * @return Mono containing the final ImportResult
     */
    private Mono<ImportResult> processUnifiedRoomRates(List<UnifiedRoomRateDto> roomRates, String sourceType, ImportType importType,
                                                       String importProcessId, String employeeId, LocalDateTime startTime) {
        if (roomRates.isEmpty()) {
            return Mono.just(ImportResult.builder()
                    .success(false)
                    .importProcessId(importProcessId)
                    .sourceType(sourceType)
                    .importType(importType)
                    .startTime(startTime)
                    .endTime(LocalDateTime.now())
                    .totalCount(0)
                    .message("No room rates to process")
                    .build());
        }

        return preloadReferenceData(roomRates, importType, importProcessId, employeeId)
                .doOnNext(cache -> applyAgencyAliasIfNeeded(roomRates, cache))
                .doOnNext(cache -> applyBookingGroupKey(roomRates, cache))
                .flatMap(cache -> validateRoomRates(roomRates, cache, importProcessId)
                        .map(validationResult -> Tuples.of(cache, validationResult)))
                .flatMap(tuple -> {
                    ReferenceDataCache cache = tuple.getT1();
                    ValidationResult validationResult = tuple.getT2();

                    if (validationResult.hasBlockingErrors()) {
                        // If there are errors, return result with errors
                        return Mono.just(ImportResult.builder()
                                .success(false)
                                .importProcessId(importProcessId)
                                .sourceType(sourceType)
                                .importType(importType)
                                .startTime(startTime)
                                .endTime(LocalDateTime.now())
                                .totalCount(roomRates.size())
                                .processedCount(0)
                                .errors(validationResult.getErrors())
                                .message("Import failed due to validation errors")
                                .build());
                    }

                    // If there are no errors, process room rates
                    return processValidRoomRates(validationResult.getValidRoomRates(), sourceType,  importType, cache.getProcessedStatus(),
                            cache.getInvoiceType(), importProcessId,startTime, roomRates.size());
                });
    }

    /**
     * Loads master data required for validation and transformation of room rates.
     *
     * @param roomRates Room rates being processed
     * @param importType Type of import (VIRTUAL, NO_VIRTUAL, etc.)
     * @param importProcessId Unique identifier for the import session
     * @param employeeId Employee ID performing the import
     * @return Mono containing the populated reference data cache
     */
    private Mono<ReferenceDataCache> preloadReferenceData(List<UnifiedRoomRateDto> roomRates, ImportType importType, String importProcessId,
                                                          String employeeId) {
        log.debug("Preloading reference data for process: {}", importProcessId);
        progressService.updateProgress(importProcessId, "LOADING_REFERENCE_DATA", 15, "Loading master data");

        return bulkDataLoader.preloadReferenceData(roomRates, importType, employeeId)
                .doOnSuccess(cache -> {
                    log.info("Reference data loaded: {}", cache.getStats());
                    progressService.updateProgress(importProcessId, "REFERENCE_DATA_LOADED", 25,
                            "Master data loaded successfully");
                });
    }

    /**
     * Applies agency alias logic to unify room rates under a shared alias code.
     * <p>
     * If the agency alias exists (not null, not "000"), the agencyCode is replaced by the code portion of the alias
     * (i.e., for "CODE-description", "CODE" is used; for "CODE", "CODE" is used). If the alias code does not exist in
     * the cache, the agency code is not updated.
     * <p>
     * If the alias does not contain a hyphen, the full alias string is treated as the code.
     *
     * @param unifiedDtos List of room rate DTOs to update
     * @param cache     Reference data cache containing agency information
     */
    private void applyAgencyAliasIfNeeded(List<UnifiedRoomRateDto> unifiedDtos, ReferenceDataCache cache) {
        if (unifiedDtos == null || unifiedDtos.isEmpty()) return;
        if (cache == null || cache.getAgencies() == null || cache.getAgencies().isEmpty()) return;

        Map<String, ManageAgencyDto> agencies = cache.getAgencies();

        for (UnifiedRoomRateDto dto : unifiedDtos) {
            String agencyCode = dto.getAgencyCode();
            if (agencyCode == null) continue;

            var agency = agencies.get(agencyCode);
            if (agency == null) continue;

            String alias = agency.getAgencyAlias();
            if (alias == null || alias.equalsIgnoreCase("000")) continue;

            // Extract code before hyphen if present, else the full alias
            String aliasCode = alias.split("-", 2)[0].trim();
            if (aliasCode.isEmpty() || aliasCode.equalsIgnoreCase(agencyCode)) continue;
            if (!agencies.containsKey(aliasCode)) continue;

            dto.setAgencyCode(aliasCode);
        }
    }

    private void applyBookingGroupKey(List<UnifiedRoomRateDto> unifiedDtos, ReferenceDataCache cache) {
        if (unifiedDtos == null || unifiedDtos.isEmpty()) return;
        if (cache == null || cache.getAgencies() == null || cache.getAgencies().isEmpty()) return;

        Map<String, ManageAgencyDto> agencies = cache.getAgencies();
        Map<String, ManageHotelDto> hotels = cache.getHotels();

        for (UnifiedRoomRateDto dto : unifiedDtos) {
            String agencyCode = dto.getAgencyCode();
            if (agencyCode == null) continue;

            var agency = agencies.get(agencyCode);
            if (agency == null) continue;

            String hotelCode = dto.getHotelCode();
            if (agencyCode == null) continue;

            var hotel = hotels.get(hotelCode);
            if (hotel == null) continue;
            if (hotel.isVirtual()) {
                dto.setGenerationType(EGenerationType.ByHotelInvoiceNumber);
            }
            else {
                dto.setGenerationType(agency.getGenerationType());
            }
        }
    }

    /**
     * Validates room rate records using business rules.
     *
     * @param roomRates List of unified room rate DTOs to validate
     * @param cache Reference data used in validation rules
     * @param importProcessId Identifier of the import session
     * @return Mono containing the result of validation
     */
    private Mono<ValidationResult> validateRoomRates(List<UnifiedRoomRateDto> roomRates, ReferenceDataCache cache, String importProcessId) {
        log.debug("Validating {} room rates for process: {}", roomRates.size(), importProcessId);
        progressService.updateProgress(importProcessId, "VALIDATING", 30, "Validating room rates");

        return businessRuleValidator.validateWithBusinessRules(roomRates, cache)
                .doOnSuccess(result -> {
                    String message = result.hasBlockingErrors() ?
                            String.format("Validation failed: %d errors found", result.getErrors().size()) :
                            String.format("Validation passed: %d room rates valid", result.getValidRoomRates().size());

                    progressService.updateProgress(importProcessId, "VALIDATION_COMPLETED", 50, message);
                });
    }

    /**
     * Sends the validated room rates to the processor for creating bookings and invoices.
     *
     * @param validRoomRates List of room rates that passed validation
     * @param sourceType Source type string (e.g., EXCEL)
     * @param importType Import type string (e.g., NONE, INVOICE_BOOKING_FROM_FILE, BOOKING_FROM_FILE_VIRTUAL_HOTEL, INSIST)
     * @param invoiceStatus ManageInvoiceStatusDto Processed
     * @param invoiceType ManageInvoiceTypeDto Invoice
     * @param importProcessId Unique identifier for the import session
     * @param startTime Start timestamp of import
     * @param totalCount Total number of room rates processed
     * @return Mono containing the final ImportResult with booking and invoice info
     */
    private Mono<ImportResult> processValidRoomRates(List<UnifiedRoomRateDto> validRoomRates, String sourceType, ImportType importType,
                                                     ManageInvoiceStatusDto invoiceStatus, ManageInvoiceTypeDto invoiceType, String importProcessId,
                                                     LocalDateTime startTime, int totalCount) {
        log.debug("Processing {} valid room rates for process: {}", validRoomRates.size(), importProcessId);
        progressService.updateProgress(importProcessId, "PROCESSING", 60, "Creating bookings and invoices");

        return roomRateProcessor.processRoomRates(validRoomRates, importType, invoiceStatus, invoiceType, importProcessId)
                .map(processingResult -> {

                    progressService.updateProgress(importProcessId, "COMPLETED", 100, "Import completed successfully");

                    return ImportResult.builder()
                            .success(true)
                            .importProcessId(importProcessId)
                            .sourceType(sourceType)
                            .startTime(startTime)
                            .endTime(LocalDateTime.now())
                            .totalCount(totalCount)
                            .processedCount(validRoomRates.size())
                            .generatedBookings(processingResult.getBookingsCreated())
                            .generatedInvoices(processingResult.getInvoicesCreated())
                            .message("Import completed successfully")
                            .build();
                })
                .doOnSuccess(result -> log.info("Processing completed for process: {}", importProcessId));
    }

    /**
     * Fallback method to handle any exception that occurred during the import process.
     *
     * @param error Throwable representing the cause of failure
     * @param importProcessId Unique ID of the import
     * @param sourceType Source type string (e.g., EXCEL)
     * @param startTime Start timestamp of import
     * @return Mono containing a failed ImportResult with error details
     */
    private Mono<ImportResult> handleImportError(Throwable error, String importProcessId, String sourceType, LocalDateTime startTime) {
        log.error("Import process {} failed", importProcessId, error);

        String errorMessage = "Import failed: " + error.getMessage();
        progressService.updateProgress(importProcessId, "FAILED", 0, errorMessage);

        return Mono.just(ImportResult.builder()
                .success(false)
                .importProcessId(importProcessId)
                .sourceType(sourceType)
                .startTime(startTime)
                .endTime(LocalDateTime.now())
                .totalCount(0)
                .processedCount(0)
                .message(errorMessage)
                .build());
    }

    /**
     * Get information about available adapters
     * @return
     */
    public Map<String, String> getAvailableAdapters() {
        return adapters.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getAdapterInfo().getDescription()
                ));
    }
}
