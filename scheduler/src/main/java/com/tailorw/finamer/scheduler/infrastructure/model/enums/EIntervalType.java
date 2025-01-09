package com.tailorw.finamer.scheduler.infrastructure.model.enums;

public enum EIntervalType {
    MINUTE,
    HOUR,
    ONE_TIME;

    public static boolean exists(String value) {
        if (value == null) {
            return false;
        }
        try {
            EIntervalType.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static EIntervalType getCode(String value){
        if (value == null) {
            return null;
        }
        try {
            return EIntervalType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
