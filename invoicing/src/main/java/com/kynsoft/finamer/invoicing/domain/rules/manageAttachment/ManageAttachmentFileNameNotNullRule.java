package com.kynsoft.finamer.invoicing.domain.rules.manageAttachment;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageAttachmentFileNameNotNullRule extends BusinessRule {
    private final String url;

    public ManageAttachmentFileNameNotNullRule(String url) {
        super(
                DomainErrorMessage.MANAGE_ATTACHMENT_URL_CANNOT_BE_EMPTY,
                new ErrorField("file", DomainErrorMessage.MANAGE_ATTACHMENT_URL_CANNOT_BE_EMPTY.getReasonPhrase())
        );
        this.url = url;
    }

    @Override
    public boolean isBroken() {
        return this.url == null || this.url.isEmpty();
    }
}
