package com.kynsof.share.core.infrastructure.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class DateUtil {

    private static final String [] validDateFormat = new String[]{"yyyyMMdd","yyyy/MM/dd"};

    public static LocalDateTime parseDateToDateTime(String date){
        LocalDate localDate;
        if (date.length() ==8){
            localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(validDateFormat[0]));
            return localDate.atTime(0,0);
        }
        if (date.length() == 10){
            localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(validDateFormat[1]));
            return localDate.atTime(0,0);
        }
        return null;
    }

    public static LocalDate parseDateToLocalDate(String date){
        LocalDate localDate;
        if (date.length() ==8){
            localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(validDateFormat[0]));
            return localDate;
        }
        if (date.length() == 10){
            localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(validDateFormat[1]));
            return localDate;
        }
        return null;
    }
    public static LocalDate parseDateToLocalDate(String date,String dateFormat){
        if (Objects.isNull(dateFormat) || dateFormat.isEmpty()){
            return parseDateToLocalDate(date);
        }
        LocalDate localDate;
        if (date.length() ==8){
            localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormat));
            return localDate;
        }
        if (date.length() == 10){
            localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormat));
            return localDate;
        }
        return null;
    }

    public static boolean validateDateFormat(String date){
        try {
            if (date.length() ==8){
                LocalDate.parse(date, DateTimeFormatter.ofPattern(validDateFormat[0]));
                return true;
            }
            if (date.length() == 10){
                LocalDate.parse(date, DateTimeFormatter.ofPattern(validDateFormat[1]));
                return true;
            }
        }catch (DateTimeParseException exception){
            return false;
        }
        return false;

    }
    public static boolean validateDateFormat(String date,String dateFormat){
        if (Objects.isNull(dateFormat) || dateFormat.isEmpty()){
            return validateDateFormat(date);
        }
        try {
            if (date.length() ==8){
                LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormat));
                return true;
            }
            if (date.length() == 10){
                LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormat));
                return true;
            }
        }catch (DateTimeParseException exception){
            return false;
        }
        return false;

    }
}
