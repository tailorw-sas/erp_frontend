package com.kynsoft.finamer.invoicing.infrastructure.utils;

import com.kynsoft.finamer.invoicing.domain.dto.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
public class ImportDataCache {
    private HashMap<String, ManageHotelDto> hotelMap;
    private HashMap<String, ManageAgencyDto> agencyMap;
    private HashMap<String, ManageRoomTypeDto> roomTypeMap;
    private HashMap<String, ManageRatePlanDto> ratePlanMap;
    private HashMap<String, ManageNightTypeDto> nightTypeMap;
    private HashMap<String, ManageBookingDto> bookingMap;
    private HashMap<String, InvoiceCloseOperationDto> closeOperationMap;
//BookingImportCacheRedisRepository
}
