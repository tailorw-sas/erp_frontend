package com.kynsoft.report.infrastructure.enums;

public enum JasperParameterCategory {
    REPORT,        // El parámetro es consumido directamente por Jasper
    COMPLEMENTARY;  // El parámetro es auxiliar para el frontend / UI

    public static JasperParameterCategory fromString(String value) {
        for (JasperParameterCategory type : JasperParameterCategory.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported parameter category: " + value);
    }
}
