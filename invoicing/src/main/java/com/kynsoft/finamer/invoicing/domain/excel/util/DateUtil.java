package com.kynsoft.finamer.invoicing.domain.excel.util;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {

    private static final String [] validDateFormat = new String[]{"yyyyMMdd","MM/dd/yyyy"};

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
        try {
            if (date.length() ==8){
                localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(validDateFormat[0]));
                return localDate;
            }
            if (date.length() == 10){
                localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(validDateFormat[1]));
                return localDate;
            }
        }catch (Exception e){
            return null;
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
        }catch (Exception exception){
            return false;
        }
        return false;

    }
}
