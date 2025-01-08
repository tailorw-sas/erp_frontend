package com.tailorw.finamer.scheduler.infrastructure.model.enums;

public enum EFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    ANNUALLY,
    ONE_TIME;

    public static boolean exists(String value) {
        if (value == null) {
            return false;
        }
        try {
            EFrequency.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static EFrequency getCode(String value){
        if (value == null) {
            return null;
        }
        try {
            return EFrequency.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
