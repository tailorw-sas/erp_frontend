package com.kynsoft.finamer.invoicing.domain.excel.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final String[] validDateFormat = new String[]{"yyyyMMdd", "MM/dd/yyyy"};

    public static LocalDateTime parseDateToDateTime(String date) {
        date = date.trim();
        LocalDate localDate;
        try {
            if (date.length() == 8) {
                localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(validDateFormat[0]));
                return localDate.atTime(0, 0);
            }
            if (date.length() == 10) {
                localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(validDateFormat[1]));
                return localDate.atTime(0, 0);
            }
        } catch (Exception e) {
            System.err.println("Error parsing date to DateTime: " + e.getMessage());
        }
        return null;
    }

    public static LocalDate parseDateToLocalDate(String date) {
        date = date.trim();
        LocalDate localDate;
        try {
            if (date.length() == 8) {
                localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(validDateFormat[0]));
                return localDate;
            }
            if (date.length() == 10) {
                localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(validDateFormat[1]));
                return localDate;
            }
        } catch (Exception e) {
            System.err.println("Error parsing date to DateTime: " + e.getMessage());
            return null;
        }
        return null;
    }

    public static boolean validateDateFormat(String date) {
        date = date.trim();
        try {
            if (date.length() == 8) {
                LocalDate.parse(date, DateTimeFormatter.ofPattern(validDateFormat[0]));
                return true;
            }
            if (date.length() == 10) {
                LocalDate.parse(date, DateTimeFormatter.ofPattern(validDateFormat[1]));
                return true;
            }
        } catch (Exception exception) {
            System.err.println("Error parsing date to DateTime: " + exception.getMessage());
            return false;
        }
        return false;

    }
}
