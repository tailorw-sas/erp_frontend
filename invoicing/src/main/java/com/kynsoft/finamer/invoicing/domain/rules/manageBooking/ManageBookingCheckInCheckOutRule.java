package com.kynsoft.finamer.invoicing.domain.rules.manageBooking;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.time.LocalDateTime;

public class ManageBookingCheckInCheckOutRule extends BusinessRule {

    private final LocalDateTime checkIn;
    private final LocalDateTime checkOut;

    public ManageBookingCheckInCheckOutRule(LocalDateTime checkIn, LocalDateTime checkOut) {
        super(
                DomainErrorMessage.MANAGE_ROOM_RATE_CHECKHIN_CHECKOUT, 
                new ErrorField("date", DomainErrorMessage.MANAGE_ROOM_RATE_CHECKHIN_CHECKOUT.getReasonPhrase())
        );
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    @Override
    public boolean isBroken() {
        return checkIn.isAfter(checkOut);
    }

}
