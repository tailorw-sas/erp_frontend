package com.kynsoft.finamer.insis.infrastructure.model.enums;

public enum BatchStatus {
    START,
    PROCESS,
    END;

    public static BatchStatus convertToBatchStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("BatchStatus cannot be null or blank");
        }

        try {
            return BatchStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid BatchStatus: " + status, e);
        }
    }
}
