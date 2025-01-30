package com.tailorw.finamer.scheduler.infrastructure.model.enums;

public enum EProcessingDateType {
    TODAY,
    YESTERDAY,
    BEFORE_YESTERDAY,
    FIRST_DAY_OF_WEEK,
    FIRST_DAY_OF_THE_MONTH,
    FIRST_DAY_OF_THE_YEAR,
    LAST_DAY_OF_THE_PREVIOUS_WEEK,
    LAST_DAY_OF_THE_PREVIOUS_MONTH,
    LAST_DAY_OF_THE_PREVIOUS_YEAR,
    LAST_MONDAY,
    LAST_TUESDAY,
    LAST_WEDNESDAY,
    LAST_THURSDAY,
    LAST_FRIDAY,
    LAST_SATURDAY,
    LAST_SUNDAY,
    LAST_MONTH,
    LAST_YEAR,
    LAST_WEEK,
    DAY,
    MONTH,
    YEAR,
    CUSTOM;

    public static boolean exists(String value) {
        if (value == null) {
            return false;
        }
        try {
            EProcessingDateType.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static EProcessingDateType getCode(String value) {
        if (value == null) {
            return null;
        }
        try {
            return EProcessingDateType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
