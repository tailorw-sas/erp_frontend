package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;

public class CheckPaymentDetailAmountGreaterThanZeroRule extends BusinessRule {

    private final PaymentDto payment;
    private final boolean isFromCredit;
    private final Double amount;

    public CheckPaymentDetailAmountGreaterThanZeroRule(PaymentDto payment, boolean isFromCredit, Double amount) {
        super(DomainErrorMessage.CHECK_PAYMENT_DETAILS_AMOUNT_GREATER_THAN_ZERO, new ErrorField("amount", DomainErrorMessage.CHECK_PAYMENT_DETAILS_AMOUNT_GREATER_THAN_ZERO.getReasonPhrase()));
        this.payment = payment;
        this.isFromCredit = isFromCredit;
        this.amount = amount;
    }

    /**
     * Payment Detail Amount: Debe ser mayor estricto que 0.
     * @return true or false
     */
    @Override
    public boolean isBroken() {
        if(payment.getPaymentSource().getExpense() && isFromCredit){
            return false;
        }
        return this.amount <= 0;
    }

}
