package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckGreaterThanOrEqualToTheTransactionAmountRule extends BusinessRule {

    private final Double amount;
    private final Double amountTransactionDeposit;

    public CheckGreaterThanOrEqualToTheTransactionAmountRule(Double amount, Double amountTransactionDeposit) {
        super(DomainErrorMessage.CHECK_AMOUNT_LESS_OR_EQUAL_TRANSACTION_AMOUNT, new ErrorField("amount", DomainErrorMessage.CHECK_AMOUNT_LESS_OR_EQUAL_TRANSACTION_AMOUNT.getReasonPhrase()));
        this.amount = amount;
        //this.amountTransactionDeposit = amountTransactionDeposit * -1;
        this.amountTransactionDeposit = amountTransactionDeposit;
    }

    /**
     * Payment Detail Amount: Debe ser mayor estricto que 0.
     * @return true or false
     */
    @Override
    public boolean isBroken() {
        return this.amount > this.amountTransactionDeposit;
    }

}
