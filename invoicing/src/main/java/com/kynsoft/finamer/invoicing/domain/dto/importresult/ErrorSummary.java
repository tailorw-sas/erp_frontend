package com.kynsoft.finamer.invoicing.domain.dto.importresult;

import com.kynsoft.finamer.invoicing.domain.dto.validation.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Resumen de errores agrupados por fuente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorSummary {
    private String source;              // "EXCEL - Row 15", "EXTERNAL_SYSTEM_A - Object ID: 12345"
    private int errorCount;
    private List<ValidationError> errors;

    /**
     * Obtiene los códigos de error únicos en este grupo
     */
    public List<String> getUniqueErrorCodes() {
        return errors.stream()
                .map(ValidationError::getErrorCode)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Indica si hay errores críticos en este grupo
     */
    public boolean hasCriticalErrors() {
        return errors.stream()
                .anyMatch(ValidationError::isBlocking);
    }
}
