package com.kynsoft.finamer.creditcard.domain.rules.manageBankReconciliation;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.List;

public class BankReconciliationListOfAmountDetailsRule extends BusinessRule {

    private final Double amount;

    private final Double details;

    private final List<Double> transactionAmounts;

    public BankReconciliationListOfAmountDetailsRule(Double amount, Double details, List<Double> transactionAmounts) {
        super(
                DomainErrorMessage.MANAGE_BANK_RECONCILIATION_AMOUNT_DETAILS,
                new ErrorField("amount", DomainErrorMessage.MANAGE_BANK_RECONCILIATION_AMOUNT_DETAILS.getReasonPhrase()));
        this.amount = amount;
        this.details = details;
        this.transactionAmounts = transactionAmounts;
    }

    @Override
    public boolean isBroken() {
        double amounts = transactionAmounts.stream().reduce(0.0, Double::sum);
        return amounts + this.details > this.amount;
    }
}
