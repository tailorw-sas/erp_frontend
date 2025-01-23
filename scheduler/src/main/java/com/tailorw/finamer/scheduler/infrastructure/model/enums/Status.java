package com.tailorw.finamer.scheduler.infrastructure.model.enums;

public enum Status {
    ACTIVE,
    INACTIVE,
    DELETED;

    public static boolean exists(String value) {
        if (value == null) {
            return false;
        }
        try {
            Status.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static Status getCode(String value) {
        try {
            return Status.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status code is not valid");
        }
    }
}
