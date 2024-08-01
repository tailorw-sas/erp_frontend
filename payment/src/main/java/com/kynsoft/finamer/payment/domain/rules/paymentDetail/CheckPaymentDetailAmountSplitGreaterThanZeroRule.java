package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckPaymentDetailAmountSplitGreaterThanZeroRule extends BusinessRule {

    private final Double amount;

    public CheckPaymentDetailAmountSplitGreaterThanZeroRule(Double amount) {
        super(DomainErrorMessage.CHECK_SPLIT_DEPOSIT_BALANCE_GREATER_THAN_ZERO, new ErrorField("amount", DomainErrorMessage.CHECK_SPLIT_DEPOSIT_BALANCE_GREATER_THAN_ZERO.getReasonPhrase()));
        this.amount = amount;
    }

    /**
     * Debe ser mayor estricto que 0.
     * @return true or false
     */
    @Override
    public boolean isBroken() {
        return this.amount <= 0;
    }

}
