package com.kynsoft.finamer.payment.domain.rules.masterPaymentAttachment;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class MasterPaymetAttachmentWhitDefaultTrueIntoCreateMustBeUniqueRule extends BusinessRule {

    private final Integer countDefault;

    public MasterPaymetAttachmentWhitDefaultTrueIntoCreateMustBeUniqueRule(Integer countDefault) {
        super(
                DomainErrorMessage.ATTACHMENT_TYPE_CHECK_DEFAULT,
                new ErrorField("defaults", DomainErrorMessage.ATTACHMENT_TYPE_CHECK_DEFAULT.getReasonPhrase())
        );
        this.countDefault = countDefault;
    }

    @Override
    public boolean isBroken() {
        return this.countDefault > 1;
    }

}
