package com.kynsoft.finamer.invoicing.domain.interfaces;

import java.util.List;

/**
 * Información sobre un adaptador específico
 */

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class AdapterInfo {
    private String sourceType;
    private String description;
    private Class<?> expectedSourceClass;
    private List<String> requiredFields;
    private String version;

    /**
     * Crea información básica para un adaptador
     */
    public static AdapterInfo create(String sourceType, String description, Class<?> expectedClass) {
        return AdapterInfo.builder()
                .sourceType(sourceType)
                .description(description)
                .expectedSourceClass(expectedClass)
                .version("1.0")
                .build();
    }
}
