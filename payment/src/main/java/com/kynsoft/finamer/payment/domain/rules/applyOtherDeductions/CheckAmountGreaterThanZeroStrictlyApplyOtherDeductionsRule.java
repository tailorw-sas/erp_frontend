package com.kynsoft.finamer.payment.domain.rules.applyOtherDeductions;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckAmountGreaterThanZeroStrictlyApplyOtherDeductionsRule extends BusinessRule {

    private final Double amount;
    private final Double bookingBalance;

    public CheckAmountGreaterThanZeroStrictlyApplyOtherDeductionsRule(Double amount, Double bookingBalance) {
        super(DomainErrorMessage.BOOKING_CHECK_AMOUNT_GREATER_THAN_ZERO_STRICTLY, new ErrorField("bookingBalance", DomainErrorMessage.BOOKING_CHECK_AMOUNT_GREATER_THAN_ZERO_STRICTLY.getReasonPhrase()));
        this.amount = amount;
        this.bookingBalance = bookingBalance;
    }

    @Override
    public boolean isBroken() {
        return this.amount <= 0 || this.amount > this.bookingBalance;
    }

}
