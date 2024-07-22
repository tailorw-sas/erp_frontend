package com.kynsoft.finamer.settings.domain.rules.managePaymentTransactionType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManagePaymentTransantionTypeValidateCashRule extends BusinessRule {

    private final Boolean cash;
    private final Boolean deposit;
    private final Boolean applyDeposit;

    public ManagePaymentTransantionTypeValidateCashRule(Boolean cash, Boolean deposit, Boolean applyDeposit) {
        super(
                DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_CASH,
                new ErrorField("code", DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_CASH.getReasonPhrase())
        );
        this.cash = cash;
        this.deposit = deposit;
        this.applyDeposit = applyDeposit;
    }

    @Override
    public boolean isBroken() {
        if (cash) {
            return deposit || applyDeposit;
        }
        return false;
    }

}
