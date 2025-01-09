package com.tailorw.tcaInnsist.infrastructure.service.utils;

import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;
import org.apache.commons.lang3.StringUtils;

public class BookingUtils {
    private static final String ID_FIELD_SEPARATOR = "|";

    public static String generateBookingId(ManageHotelDto hotel, String groupedKey, String checkInDate, String checkOutDate){
        return hotel.getCode().concat(ID_FIELD_SEPARATOR).
                concat(groupedKey).concat(ID_FIELD_SEPARATOR).
                concat(checkInDate).concat(ID_FIELD_SEPARATOR).
                concat(checkOutDate);
    }

    public static String[] generateFieldsFromBookingId(String id){
        if(StringUtils.isNotEmpty(id) && StringUtils.isNotBlank(id)){
           return id.split(ID_FIELD_SEPARATOR);
        }

        return null;
    }
}
