package com.kynsoft.finamer.invoicing.infrastructure.services.orchestrator;

import com.kynsoft.finamer.invoicing.domain.dto.importresult.ImportResult;
import com.kynsoft.finamer.invoicing.domain.dto.roomrate.UnifiedRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.dto.validation.ValidationResult;
import com.kynsoft.finamer.invoicing.domain.dto.cache.ReferenceDataCache;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Orquestador principal que coordina todo el proceso de importación de Room Rates
 * desde múltiples fuentes (Excel, APIs externas, etc.) hasta la generación de facturas.
 *
 * Maneja el flujo completo: Adaptación → Validación → Procesamiento
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
     * Constructor que usa @Autowired para inyección automática
     */
    public UnifiedRoomRateImportOrchestrator(
            @Autowired(required = false) List<RoomRateSourceAdapter> adapterList,
            BulkDataLoader bulkDataLoader,
            BusinessRuleValidator businessRuleValidator,
            RoomRateProcessor roomRateProcessor,
            ImportProgressService progressService,
            @Qualifier("importExecutor") TaskExecutor importExecutor) {

        // Registrar adaptadores por tipo (manejar lista vacía si no hay adaptadores)
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

        log.info("Initialized orchestrator with {} adapters: {}",
                adapters.size(), adapters.keySet());
    }

    /**
     * Procesa importación desde cualquier fuente de forma asíncrona
     *
     * @param source Datos de origen (List<BookingRow>, List<ExternalDto>, etc.)
     * @param sourceType Tipo de fuente ("EXCEL", "EXTERNAL_SYSTEM_A", etc.)
     * @param importProcessId ID único del proceso
     * @param employee ID del empleado que ejecuta la importación
     * @param importType Tipo de importación (VIRTUAL, NO_VIRTUAL, INNSIST)
     * @return Resultado completo del proceso
     */
    @Async("importExecutor")
    public Mono<ImportResult> processImport(Object source, String sourceType,
                                            String importProcessId, String employee, String importType) {
        log.info("Starting import process {} from source type: {}, importType: {}",
                importProcessId, sourceType, importType);
        LocalDateTime startTime = LocalDateTime.now();

        return processUnifiedImport(source, sourceType, importProcessId, employee, importType, startTime)
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
     * Flujo principal de procesamiento unificado
     */
    private Mono<ImportResult> processUnifiedImport(Object source, String sourceType, String importProcessId, String employee, String importType,
                                                    LocalDateTime startTime) {

        return initializeImport(importProcessId, sourceType, startTime)
                .flatMap(result -> adaptSourceToUnified(source, sourceType, importProcessId))
                .flatMap(roomRates -> processUnifiedRoomRates(roomRates, employee, importProcessId, sourceType, importType, startTime))
                .onErrorResume(error -> handleImportError(error, importProcessId, sourceType, startTime));
    }

    /**
     * Inicializa el proceso de importación
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
     * Adapta los datos de origen al formato unificado
     */
    private Mono<List<UnifiedRoomRateDto>> adaptSourceToUnified(Object source, String sourceType,
                                                                String importProcessId) {
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
     * Procesa room rates unificados a través de todo el pipeline
     */
    private Mono<ImportResult> processUnifiedRoomRates(List<UnifiedRoomRateDto> roomRates, String employeeId,
                                                       String importProcessId, String sourceType, String importType,
                                                       LocalDateTime startTime) {

        if (roomRates.isEmpty()) {
            return Mono.just(ImportResult.builder()
                    .success(false)
                    .importProcessId(importProcessId)
                    .sourceType(sourceType)
                    .startTime(startTime)
                    .endTime(LocalDateTime.now())
                    .totalCount(0)
                    .message("No room rates to process")
                    .build());
        }

        return preloadReferenceData(roomRates, employeeId, importType, importProcessId)
                .flatMap(cache -> validateRoomRates(roomRates, cache, importProcessId))
                .flatMap(validationResult -> {

                    if (validationResult.hasBlockingErrors()) {
                        // Si hay errores, retornar resultado con errores
                        return Mono.just(ImportResult.builder()
                                .success(false)
                                .importProcessId(importProcessId)
                                .sourceType(sourceType)
                                .startTime(startTime)
                                .endTime(LocalDateTime.now())
                                .totalCount(roomRates.size())
                                .processedCount(0)
                                .errors(validationResult.getErrors())
                                .message("Import failed due to validation errors")
                                .build());
                    }

                    // Si no hay errores, procesar room rates
                    return processValidRoomRates(validationResult.getValidRoomRates(), importProcessId,
                            sourceType, startTime, roomRates.size());
                });
    }

    /**
     * Precarga todos los datos de referencia necesarios
     */
    private Mono<ReferenceDataCache> preloadReferenceData(List<UnifiedRoomRateDto> roomRates, String employeeId, String importType,
                                         String importProcessId) {
        log.debug("Preloading reference data for process: {}", importProcessId);
        progressService.updateProgress(importProcessId, "LOADING_REFERENCE_DATA", 15, "Loading master data");

        return bulkDataLoader.preloadReferenceData(roomRates, employeeId, importType)
                .doOnSuccess(cache -> {
                    log.info("Reference data loaded: {}", cache.getStats());
                    progressService.updateProgress(importProcessId, "REFERENCE_DATA_LOADED", 25,
                            "Master data loaded successfully");
                });
    }

    /**
     * Ejecuta validación completa con reglas de negocio
     */
    private Mono<ValidationResult> validateRoomRates(List<UnifiedRoomRateDto> roomRates, ReferenceDataCache cache,
                                                     String importProcessId) {
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
     * Procesa room rates válidos para crear bookings e invoices
     */
    private Mono<ImportResult> processValidRoomRates(List<UnifiedRoomRateDto> validRoomRates, String importProcessId,
                                                     String sourceType, LocalDateTime startTime, int totalCount) {
        log.debug("Processing {} valid room rates for process: {}", validRoomRates.size(), importProcessId);
        progressService.updateProgress(importProcessId, "PROCESSING", 60, "Creating bookings and invoices");

        return roomRateProcessor.processRoomRates(validRoomRates, importProcessId)
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
     * Maneja errores durante el proceso de importación
     */
    private Mono<ImportResult> handleImportError(Throwable error, String importProcessId,
                                                 String sourceType, LocalDateTime startTime) {
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
     * Obtiene información sobre adaptadores disponibles
     */
    public Map<String, String> getAvailableAdapters() {
        return adapters.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getAdapterInfo().getDescription()
                ));
    }
}