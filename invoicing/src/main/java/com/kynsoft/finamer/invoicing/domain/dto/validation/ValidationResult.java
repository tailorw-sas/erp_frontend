package com.kynsoft.finamer.invoicing.domain.dto.validation;

import com.kynsoft.finamer.invoicing.domain.dto.roomrate.UnifiedRoomRateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
    private List<UnifiedRoomRateDto> validRoomRates;
    private List<ValidationError> errors;
    private int totalProcessed;
    private long validationTimeMs;

    /**
     * Indica si hay errores que bloquean el procesamiento
     */
    public boolean hasBlockingErrors() {
        return errors != null && errors.stream().anyMatch(ValidationError::isBlocking);
    }

    /**
     * Indica si hay algún tipo de error
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    /**
     * Retorna solo los errores que bloquean el procesamiento
     */
    public List<ValidationError> getBlockingErrors() {
        if (errors == null) return Collections.emptyList();
        return errors.stream()
                .filter(ValidationError::isBlocking)
                .collect(Collectors.toList());
    }

    /**
     * Retorna solo las advertencias (no bloquean procesamiento)
     */
    public List<ValidationError> getWarnings() {
        if (errors == null) return Collections.emptyList();
        return errors.stream()
                .filter(error -> error.getSeverity() == ValidationError.ErrorSeverity.WARNING)
                .collect(Collectors.toList());
    }

    /**
     * Agrupa errores por fuente para mejor presentación
     */
    public Map<String, List<ValidationError>> getErrorsBySource() {
        if (errors == null) return Collections.emptyMap();

        return errors.stream()
                .collect(Collectors.groupingBy(
                        error -> error.getSourceType() + " - " + error.getSourceIdentifier()
                ));
    }

    /**
     * Agrupa errores por código de error para análisis
     */
    public Map<String, List<ValidationError>> getErrorsByCode() {
        if (errors == null) return Collections.emptyMap();

        return errors.stream()
                .collect(Collectors.groupingBy(ValidationError::getErrorCode));
    }

    /**
     * Obtiene estadísticas de la validación
     */
    public ValidationStats getStats() {
        int totalErrors = errors != null ? errors.size() : 0;
        int blockingErrors = getBlockingErrors().size();
        int warnings = getWarnings().size();

        return ValidationStats.builder()
                .totalProcessed(totalProcessed)
                .validCount(validRoomRates != null ? validRoomRates.size() : 0)
                .totalErrors(totalErrors)
                .blockingErrors(blockingErrors)
                .warnings(warnings)
                .validationTimeMs(validationTimeMs)
                .successRate(totalProcessed > 0 ? (double) (totalProcessed - blockingErrors) / totalProcessed * 100 : 0)
                .build();
    }

    @Data
    @Builder
    public static class ValidationStats {
        private int totalProcessed;
        private int validCount;
        private int totalErrors;
        private int blockingErrors;
        private int warnings;
        private long validationTimeMs;
        private double successRate;
    }

    // Factory methods
    public static ValidationResult success(List<UnifiedRoomRateDto> validRoomRates,
                                           int totalProcessed, long validationTimeMs) {
        return ValidationResult.builder()
                .validRoomRates(validRoomRates)
                .errors(Collections.emptyList())
                .totalProcessed(totalProcessed)
                .validationTimeMs(validationTimeMs)
                .build();
    }

    public static ValidationResult withErrors(List<ValidationError> errors, int totalProcessed, long validationTimeMs) {
        return ValidationResult.builder()
                .validRoomRates(Collections.emptyList())
                .errors(errors)
                .totalProcessed(totalProcessed)
                .validationTimeMs(validationTimeMs)
                .build();
    }
}
