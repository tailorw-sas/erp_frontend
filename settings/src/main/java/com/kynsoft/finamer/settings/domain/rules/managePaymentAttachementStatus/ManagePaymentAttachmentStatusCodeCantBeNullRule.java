package com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManagePaymentAttachmentStatusCodeCantBeNullRule extends BusinessRule {

    private final String code;

    public ManagePaymentAttachmentStatusCodeCantBeNullRule(final String code) {
        super(DomainErrorMessage.MANAGE_PAYMENT_ATTACHMENT_STATUS_NAME_CANT_BE_NULL, new ErrorField("name", "Name can't be empty"));
        this.code = code;
    }

    @Override
    public boolean isBroken() {
        return this.code == null || this.code.isEmpty();
    }
}
