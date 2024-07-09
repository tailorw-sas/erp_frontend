package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckPaymentDetailAmountGreaterThanZeroRule extends BusinessRule {

    private final Double amount;

    public CheckPaymentDetailAmountGreaterThanZeroRule(Double amount) {
        super(DomainErrorMessage.CHECK_PAYMENT_DETAILS_AMOUNT_GREATER_THAN_ZERO, new ErrorField("amount", DomainErrorMessage.CHECK_PAYMENT_DETAILS_AMOUNT_GREATER_THAN_ZERO.getReasonPhrase()));
        this.amount = amount;
    }

    /**
     * Payment Detail Amount: Debe ser mayor estricto que 0.
     * @return true or false
     */
    @Override
    public boolean isBroken() {
        return this.amount <= 0;
    }

}
