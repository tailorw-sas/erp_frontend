package com.kynsoft.finamer.invoicing.domain.dto.importresult;

import com.kynsoft.finamer.invoicing.domain.dto.validation.ValidationError;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.ImportType;
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
 * Complete result of an import process
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult {
    private boolean success;
    private String importProcessId;
    private String sourceType;
    private ImportType importType;
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
     * Calculate the duration of the process in milliseconds
     */
    public long getDurationMs() {
        if (startTime != null && endTime != null) {
            return java.time.Duration.between(startTime, endTime).toMillis();
        }
        return 0;
    }

    /**
     * Indicates if there were errors during the process
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    /**
     * Gets the percentage of success of the processing
     */
    public double getSuccessRate() {
        if (totalCount == 0) return 0;
        return (double) processedCount / totalCount * 100;
    }

    /**
     * Group errors by source for better UI presentation
     */
    public Map<String, List<ValidationError>> getErrorsBySource() {
        if (errors == null) return Collections.emptyMap();

        return errors.stream()
                .collect(Collectors.groupingBy(
                        error -> error.getSourceType() + " - " + error.getSourceIdentifier()
                ));
    }

    /**
     * Generates an error summary to display in the UI
     * @return
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

    /**
     * Factory methods for different types of results
     * @param importProcessId
     * @param sourceType
     * @param totalCount
     * @param generatedBookings
     * @param generatedInvoices
     * @return
     */
    public static ImportResult success(String importProcessId, String sourceType, ImportType importType,
                                       int totalCount, int generatedBookings, int generatedInvoices) {
        return ImportResult.builder()
                .success(true)
                .importProcessId(importProcessId)
                .sourceType(sourceType)
                .importType(importType)
                .totalCount(totalCount)
                .processedCount(totalCount)
                .generatedBookings(generatedBookings)
                .generatedInvoices(generatedInvoices)
                .errors(Collections.emptyList())
                .message("Import completed successfully")
                .endTime(LocalDateTime.now())
                .build();
    }


    public static ImportResult failed(String importProcessId, String sourceType, ImportType importType,
                                      int totalCount, List<ValidationError> errors, String message) {
        return ImportResult.builder()
                .success(false)
                .importProcessId(importProcessId)
                .sourceType(sourceType)
                .importType(importType)
                .totalCount(totalCount)
                .processedCount(0)
                .generatedBookings(0)
                .generatedInvoices(0)
                .errors(errors)
                .message(message)
                .endTime(LocalDateTime.now())
                .build();
    }

    public static ImportResult validationFailed(String importProcessId, String sourceType, ImportType importType,
                                                int totalCount, List<ValidationError> errors) {
        return failed(importProcessId, sourceType, importType, totalCount, errors,
                "Import failed due to validation errors");
    }
}
