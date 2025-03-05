package com.kynsoft.finamer.payment.domain.rules.undoApplication;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckApplyPaymentRule extends BusinessRule {

    private final Boolean applyPayment;

    public CheckApplyPaymentRule(Boolean applyPayment) {
        super(
                DomainErrorMessage.UNDO_APPLICATION_CHECK_APPLY_PAYMENT,
                new ErrorField("applyPayment", DomainErrorMessage.UNDO_APPLICATION_CHECK_APPLY_PAYMENT.getReasonPhrase())
        );
        this.applyPayment = applyPayment;
    }

    @Override
    public boolean isBroken() {
        return !this.applyPayment;
    }

}
