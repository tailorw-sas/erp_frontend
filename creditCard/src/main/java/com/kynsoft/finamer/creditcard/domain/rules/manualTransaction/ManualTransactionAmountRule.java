package com.kynsoft.finamer.creditcard.domain.rules.manualTransaction;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManualTransactionAmountRule extends BusinessRule {

    private final Double amount;

    public ManualTransactionAmountRule(Double ammount) {
        super(
                DomainErrorMessage.AMOUNT_GREATER_THAN_ZERO,
                new ErrorField("amount", DomainErrorMessage.AMOUNT_GREATER_THAN_ZERO.getReasonPhrase())
        );
        this.amount = ammount;
    }

    @Override
    public boolean isBroken() {
        return !(amount > 0);
    }
}
