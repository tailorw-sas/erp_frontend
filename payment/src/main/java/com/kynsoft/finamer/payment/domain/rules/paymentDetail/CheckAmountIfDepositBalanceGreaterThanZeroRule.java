package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckAmountIfDepositBalanceGreaterThanZeroRule extends BusinessRule {

    private final Double amount;
    private final Double depositBalance;

    public CheckAmountIfDepositBalanceGreaterThanZeroRule(Double amount, Double depositBalance) {
        super(DomainErrorMessage.CHECK_AMOUNT_IF_DEPOSIT_BALANCE_GREATER_THAN_ZERO, new ErrorField("amount", DomainErrorMessage.CHECK_AMOUNT_IF_DEPOSIT_BALANCE_GREATER_THAN_ZERO.getReasonPhrase()));
        this.amount = amount;
        this.depositBalance = depositBalance;
    }

    /**
     * Se debe poder crear payment details de tipo check marcado Apply Deposit mientras la transacción check marcado Deposit tenga balance.
     * @return true or false
     */
    @Override
    public boolean isBroken() {
        return depositBalance == 0 || (depositBalance - amount) < 0;
    }

}
