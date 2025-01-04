package com.kynsoft.finamer.invoicing.infrastructure.excel.event.error;

import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingRowError;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ImportBookingRowErrorExtraFields {

    private final IManageHotelService hotelService;

    public ImportBookingRowErrorExtraFields(IManageHotelService hotelService) {
        this.hotelService = hotelService;
    }

    public BookingRowError addExtraFieldTrendingCompany(BookingRowError bookingRowError){
       if(bookingRowError.getErrorFields().stream().noneMatch(errorField -> "Hotel".equals(errorField.getField()))) {
           ManageHotelDto manageHotelDto = hotelService.findByCode(bookingRowError.getRow().getManageHotelCode());
           ManageTradingCompaniesDto manageTradingCompaniesDto = manageHotelDto.getManageTradingCompanies();
           if (Objects.nonNull(manageTradingCompaniesDto)) {
               bookingRowError.getRow().setTrendingCompany(manageTradingCompaniesDto.getCode());
           }
       }
       return bookingRowError;
    }

}
