package com.kynsoft.finamer.creditcard.domain.rules.transaction;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;

public class TransactionReconciliationOrPaymentRule extends BusinessRule {

    private final TransactionDto transactionDto;

    public TransactionReconciliationOrPaymentRule(TransactionDto transactionDto) {
        super(
                DomainErrorMessage.TRANSACTION_RECONCILIATION_OR_PAYMENT_RELATION,
                new ErrorField("id", "Transaction " + transactionDto.getId().toString() +" already belongs to another relationship.")
        );
        this.transactionDto = transactionDto;
    }

    @Override
    public boolean isBroken() {
        return transactionDto.getReconciliation() != null || transactionDto.getHotelPayment() != null;
    }
}
