package com.kynsoft.finamer.settings.domain.rules.managePaymentTransactionType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManagePaymentTransantionTypeValidateApplyDepositRule extends BusinessRule {

    private final Boolean cash;
    private final Boolean deposit;
    private final Boolean applyDeposit;

    public ManagePaymentTransantionTypeValidateApplyDepositRule(Boolean cash, Boolean deposit, Boolean applyDeposit) {
        super(
                DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_APPLY_DEPOSIT,
                new ErrorField("code", DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_APPLY_DEPOSIT.getReasonPhrase())
        );
        this.cash = cash;
        this.deposit = deposit;
        this.applyDeposit = applyDeposit;
    }

    @Override
    public boolean isBroken() {
        if (applyDeposit) {
            return cash || deposit;
        }
        return false;
    }

}
