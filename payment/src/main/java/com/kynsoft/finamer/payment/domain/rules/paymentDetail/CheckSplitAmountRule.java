package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckSplitAmountRule extends BusinessRule {

    private final Double amount;
    private final Double amountToSplit;

    public CheckSplitAmountRule(Double amount, Double amountToSplit) {
        super(DomainErrorMessage.CHECK_SPLIT_DEPOSIT_BALANCE_GREATER_THAN_ZERO, new ErrorField("amount", DomainErrorMessage.CHECK_SPLIT_DEPOSIT_BALANCE_GREATER_THAN_ZERO.getReasonPhrase()));
        this.amount = amount;
        this.amountToSplit = amountToSplit;
    }

    /**
     * Se debe poder dividir mientras la transacción check Deposit tenga balance.
     * @return true or false
     */
    @Override
    public boolean isBroken() {
        return amount == 0 || (amount - amountToSplit) < 0;
    }

}
