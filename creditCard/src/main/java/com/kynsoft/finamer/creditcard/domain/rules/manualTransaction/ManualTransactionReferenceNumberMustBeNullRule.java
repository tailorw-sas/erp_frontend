package com.kynsoft.finamer.creditcard.domain.rules.manualTransaction;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManualTransactionReferenceNumberMustBeNullRule extends BusinessRule {

    private final String referenceNumber;

    public ManualTransactionReferenceNumberMustBeNullRule(String referenceNumber) {
        super(
                DomainErrorMessage.MANUAL_TRANSACTION_REFERENCE_NUMBER_CANNOT_BE_EMPTY,
                new ErrorField("referenceNumber", DomainErrorMessage.MANUAL_TRANSACTION_REFERENCE_NUMBER_CANNOT_BE_EMPTY.getReasonPhrase())
        );
        this.referenceNumber = referenceNumber;
    }

    @Override
    public boolean isBroken() {
        return this.referenceNumber == null || this.referenceNumber.isEmpty();
    }

}
