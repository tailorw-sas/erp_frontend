package com.kynsoft.finamer.invoicing.domain.dto.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Representa un error de validación específico
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError {
    private String sourceType;        // "EXCEL", "EXTERNAL_SYSTEM_A", etc.
    private String sourceIdentifier;  // "Row 15", "Object ID: 12345", etc.
    private String field;             // Campo que falló la validación
    private String message;           // Mensaje descriptivo del error
    private String bookingGroupKey;   // Para errores que afectan grupos de room rates
    private ErrorSeverity severity;   // ERROR, WARNING, INFO
    private String errorCode;         // Código único para el tipo de error

    public enum ErrorSeverity {
        ERROR,   // Bloquea el procesamiento
        WARNING, // No bloquea pero debe notificarse
        INFO     // Solo información adicional
    }

    /**
     * Construye un mensaje formateado para mostrar al usuario
     */
    public String getDisplayMessage() {
        return String.format("[%s - %s] %s: %s",
                sourceType, sourceIdentifier, field, message);
    }

    /**
     * Construye un mensaje técnico para logs
     */
    public String getTechnicalMessage() {
        return String.format("ValidationError[code=%s, source=%s-%s, field=%s, message=%s]",
                errorCode, sourceType, sourceIdentifier, field, message);
    }

    /**
     * Indica si este error bloquea el procesamiento
     */
    public boolean isBlocking() {
        return severity == ErrorSeverity.ERROR;
    }

    // Factory methods para crear errores comunes
    public static ValidationError fieldRequired(String sourceType, String sourceIdentifier, String field) {
        return ValidationError.builder()
                .sourceType(sourceType)
                .sourceIdentifier(sourceIdentifier)
                .field(field)
                .message("Field is required")
                .severity(ErrorSeverity.ERROR)
                .errorCode("FIELD_REQUIRED")
                .build();
    }

    public static ValidationError invalidValue(String sourceType, String sourceIdentifier, String field, String value) {
        return ValidationError.builder()
                .sourceType(sourceType)
                .sourceIdentifier(sourceIdentifier)
                .field(field)
                .message(String.format("Invalid value: %s", value))
                .severity(ErrorSeverity.ERROR)
                .errorCode("INVALID_VALUE")
                .build();
    }

    public static ValidationError businessRuleViolation(String sourceType, String sourceIdentifier,
                                                        String field, String rule, String groupKey) {
        return ValidationError.builder()
                .sourceType(sourceType)
                .sourceIdentifier(sourceIdentifier)
                .field(field)
                .message(String.format("Business rule violation: %s", rule))
                .bookingGroupKey(groupKey)
                .severity(ErrorSeverity.ERROR)
                .errorCode("BUSINESS_RULE_VIOLATION")
                .build();
    }
}
