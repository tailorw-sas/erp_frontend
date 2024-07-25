package com.kynsoft.finamer.payment.domain.rules.masterPaymentAttachment;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;

import java.util.UUID;

public class MasterPaymetAttachmentWhitDefaultTrueMustBeUniqueRule extends BusinessRule {

    private final IMasterPaymentAttachmentService service;

    private final UUID resourceId;

    public MasterPaymetAttachmentWhitDefaultTrueMustBeUniqueRule(IMasterPaymentAttachmentService service, UUID resourceId, String fileName) {
        super(
                DomainErrorMessage.ATTACHMENT_TYPE_CHECK_DEFAULT,
                new ErrorField("defaults", DomainErrorMessage.ATTACHMENT_TYPE_CHECK_DEFAULT.getReasonPhrase())
        );
        this.service = service;
        this.resourceId = resourceId;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByResourceAndAttachmentTypeIsDefault(resourceId) > 0;
    }

}
