package com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class AdjustmentTransactionAgencyBookingFormatRule extends BusinessRule {

    private final String bookingCouponFormat;

    public AdjustmentTransactionAgencyBookingFormatRule(String bookingCouponFormat) {
        super(
                DomainErrorMessage.VCC_INVALID_BOOKING_COUPON_FORMAT,
                new ErrorField("reservationNumber", DomainErrorMessage.VCC_INVALID_BOOKING_COUPON_FORMAT.getReasonPhrase())
        );
        this.bookingCouponFormat = bookingCouponFormat;
    }

    @Override
    public boolean isBroken() {
        // Comprobar si el regex es válido
        try{
            Pattern.compile(bookingCouponFormat);
        } catch (PatternSyntaxException e){
            return true;
        }
        return false;
    }
}
