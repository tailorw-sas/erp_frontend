package com.kynsoft.finamer.invoicing.domain.rules.manageBooking;

import java.util.UUID;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;

public class ManageBookingHotelBookingNumberValidationRule extends BusinessRule {

    private final IManageBookingService service;

    private final String lastChars;

    private final UUID hotelId;

    public ManageBookingHotelBookingNumberValidationRule(IManageBookingService service, String lastChars,
                                                         UUID hotelId, String hotelBookingNumber) {
        super(
                DomainErrorMessage.BOOKING_HOTEL_NUMBER_INVALID,
                new ErrorField("hotelBookingNumber",
                        "The field Hotel Booking No. "+ hotelBookingNumber +" is repeated"));
        this.service = service;
        this.lastChars = lastChars;
        this.hotelId = hotelId;
    }

    @Override
    public boolean isBroken() {
        return this.service.existsByExactLastChars(lastChars, hotelId);
    }

}
