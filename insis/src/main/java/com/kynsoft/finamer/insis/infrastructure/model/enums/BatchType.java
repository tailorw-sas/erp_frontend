package com.kynsoft.finamer.insis.infrastructure.model.enums;

public enum BatchType {
    MANUAL,
    AUTOMATIC;

    public static BatchType convertToBatchType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("BatchType cannot be null or blank");
        }

        try {
            return BatchType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid BatchType: " + type, e);
        }
    }
}
