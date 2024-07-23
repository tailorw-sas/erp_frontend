package com.kynsoft.finamer.payment.domain.rules.income;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckAmountNotZeroRule extends BusinessRule {

    private final Double amount;

    public CheckAmountNotZeroRule(Double amount) {
        super(DomainErrorMessage.INCOME_ADJUSTMENT_AMOUNT_NOT_ZERO, new ErrorField("amount", DomainErrorMessage.INCOME_ADJUSTMENT_AMOUNT_NOT_ZERO.getReasonPhrase()));
        this.amount = amount;
    }

    @Override
    public boolean isBroken() {
        return this.amount == 0;
    }

}
