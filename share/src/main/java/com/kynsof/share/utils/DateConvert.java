package com.kynsof.share.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConvert {

    public static LocalDate convertStringToLocalDate(String date, DateTimeFormatter formatter){
        if(date == null || date.isEmpty()){
            throw new IllegalArgumentException("The field date is null or empty");
        }

        return LocalDate.parse(date, formatter);
    }

    public static String convertLocalDateToString(LocalDate date, DateTimeFormatter formatter){
        if(date == null){
            throw new IllegalArgumentException("The field date is null");
        }
        return date.format(formatter);
    }

    public static DateTimeFormatter getIsoLocalDateFormatter(){
        return DateTimeFormatter.ISO_LOCAL_DATE;
    }

    public static DateTimeFormatter getIsoLocalDateTimeWithTimeZoneFormatter(){
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    }

    public static DateTimeFormatter getIsoLocalDateTimeWithoutTimeZoneFormatter(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public static DateTimeFormatter getIsoLocalDateTimeWithoutTimeZoneFormatter(boolean includeMilis){
        return includeMilis ? DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS") : getIsoLocalDateTimeWithoutTimeZoneFormatter();
    }
}
