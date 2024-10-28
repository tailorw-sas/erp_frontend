package com.kynsoft.finamer.creditcard.domain.rules.manageBankReconciliation;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class BankReconciliationAmountDetailsRule extends BusinessRule {

    private final Double amount;

    private final Double detailsAmount;

    public BankReconciliationAmountDetailsRule(Double amount, Double detailsAmount) {
        super(
                DomainErrorMessage.MANAGE_BANK_RECONCILIATION_AMOUNT_DETAILS,
                new ErrorField("amount", DomainErrorMessage.MANAGE_BANK_RECONCILIATION_AMOUNT_DETAILS.getReasonPhrase()));
        this.amount = amount;
        this.detailsAmount = detailsAmount;
    }

    @Override
    public boolean isBroken() {
        return amount < detailsAmount;
    }
}
