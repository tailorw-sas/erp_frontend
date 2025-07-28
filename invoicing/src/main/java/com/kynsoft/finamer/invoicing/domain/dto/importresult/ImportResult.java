package com.kynsoft.finamer.invoicing.domain.dto.importresult;

import com.kynsoft.finamer.invoicing.domain.dto.validation.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Resultado completo de un proceso de importación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult {
    private boolean success;
    private String importProcessId;
    private String sourceType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int totalCount;
    private int processedCount;
    private int generatedBookings;
    private int generatedInvoices;
    private List<ValidationError> errors;
    private String message;
    private ImportStats stats;

    /**
     * Calcula la duración del proceso en milisegundos
     */
    public long getDurationMs() {
        if (startTime != null && endTime != null) {
            return java.time.Duration.between(startTime, endTime).toMillis();
        }
        return 0;
    }

    /**
     * Indica si hubo errores durante el proceso
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    /**
     * Obtiene el porcentaje de éxito del procesamiento
     */
    public double getSuccessRate() {
        if (totalCount == 0) return 0;
        return (double) processedCount / totalCount * 100;
    }

    /**
     * Agrupa errores por fuente para mejor presentación en UI
     */
    public Map<String, List<ValidationError>> getErrorsBySource() {
        if (errors == null) return Collections.emptyMap();

        return errors.stream()
                .collect(Collectors.groupingBy(
                        error -> error.getSourceType() + " - " + error.getSourceIdentifier()
                ));
    }

    /**
     * Genera un resumen de errores para mostrar en UI
     */
    public List<ErrorSummary> getErrorSummary() {
        return getErrorsBySource().entrySet().stream()
                .map(entry -> ErrorSummary.builder()
                        .source(entry.getKey())
                        .errorCount(entry.getValue().size())
                        .errors(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    // Factory methods para diferentes tipos de resultado
    public static ImportResult success(String importProcessId, String sourceType,
                                       int totalCount, int generatedBookings, int generatedInvoices) {
        return ImportResult.builder()
                .success(true)
                .importProcessId(importProcessId)
                .sourceType(sourceType)
                .totalCount(totalCount)
                .processedCount(totalCount)
                .generatedBookings(generatedBookings)
                .generatedInvoices(generatedInvoices)
                .errors(Collections.emptyList())
                .message("Import completed successfully")
                .endTime(LocalDateTime.now())
                .build();
    }

    public static ImportResult failed(String importProcessId, String sourceType,
                                      int totalCount, List<ValidationError> errors, String message) {
        return ImportResult.builder()
                .success(false)
                .importProcessId(importProcessId)
                .sourceType(sourceType)
                .totalCount(totalCount)
                .processedCount(0)
                .generatedBookings(0)
                .generatedInvoices(0)
                .errors(errors)
                .message(message)
                .endTime(LocalDateTime.now())
                .build();
    }

    public static ImportResult validationFailed(String importProcessId, String sourceType,
                                                int totalCount, List<ValidationError> errors) {
        return failed(importProcessId, sourceType, totalCount, errors,
                "Import failed due to validation errors");
    }
}
