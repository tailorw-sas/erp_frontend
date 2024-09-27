package com.kynsoft.finamer.payment.domain.rules.undoApplication;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckApplyPaymentRule extends BusinessRule {

    private final Boolean applayPayment;

    public CheckApplyPaymentRule(Boolean applayPayment) {
        super(
                DomainErrorMessage.UNDO_APPLICATION_CHECK_APPLY_PAYMENT,
                new ErrorField("applyPayment", DomainErrorMessage.UNDO_APPLICATION_CHECK_APPLY_PAYMENT.getReasonPhrase())
        );
        this.applayPayment = applayPayment;
    }

    @Override
    public boolean isBroken() {
        return !this.applayPayment;
    }

}
