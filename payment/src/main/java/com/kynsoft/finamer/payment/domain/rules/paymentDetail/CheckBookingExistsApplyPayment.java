package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;

import java.util.Objects;

public class CheckBookingExistsApplyPayment extends BusinessRule {

    private final Boolean applyPayment;
    private final ManageBookingDto booking;

    public CheckBookingExistsApplyPayment(Boolean applyPayment,
                                          ManageBookingDto booking){
        super(DomainErrorMessage.BOOKING_NOT_FOUND, new ErrorField("Booking", DomainErrorMessage.BOOKING_NOT_FOUND.getReasonPhrase()));
        this.booking = booking;
        this.applyPayment = applyPayment;
    }

    @Override
    public boolean isBroken() {
        return (applyPayment && Objects.isNull(this.booking));
    }
}
