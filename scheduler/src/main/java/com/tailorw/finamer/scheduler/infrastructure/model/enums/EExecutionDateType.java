package com.tailorw.finamer.scheduler.infrastructure.model.enums;

public enum EExecutionDateType {

    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY,
    CUSTOM,
    FIRST_DAY_OF_THE_MONTH,
    LAST_DAY_OF_THE_MONTH,
    FIRST_DAY_OF_THE_YEAR,
    LAST_DAY_OF_THE_YEAR,
    SPECIFIC_DATE_OF_THE_MONTH,
    SPECIFIC_DATE_OF_THE_YEAR;

    public static boolean exists(String value) {
        if (value == null) {
            return false;
        }
        try {
            EExecutionDateType.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static EExecutionDateType getCode(String value){
        if (value == null) {
            return null;
        }
        try {
            return EExecutionDateType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
