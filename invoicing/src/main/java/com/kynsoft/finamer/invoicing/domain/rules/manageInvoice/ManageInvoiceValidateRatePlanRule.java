package com.kynsoft.finamer.invoicing.domain.rules.manageInvoice;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRatePlanDto;

public class ManageInvoiceValidateRatePlanRule extends BusinessRule {

    private final ManageHotelDto hotelDto;
    private final ManageRatePlanDto manageRatePlanDto;
    private final String hotelBookingNumber;

    public ManageInvoiceValidateRatePlanRule(ManageHotelDto hotelDto, ManageRatePlanDto manageRatePlanDto, String hotelBookingNumber) {
        super(
                DomainErrorMessage.HOTEL_RATE_PLAN_NOT_FOUND, 
                new ErrorField("ratePlan", DomainErrorMessage.HOTEL_RATE_PLAN_NOT_FOUND.getReasonPhrase() +
                        " Hotel Booking Number: "+hotelBookingNumber)
        );
        this.hotelDto = hotelDto;
        this.manageRatePlanDto = manageRatePlanDto;
        this.hotelBookingNumber = hotelBookingNumber;
    }

    @Override
    public boolean isBroken() {
        return !hotelDto.getCode().equals(manageRatePlanDto.getHotel().getCode());
    }

}
