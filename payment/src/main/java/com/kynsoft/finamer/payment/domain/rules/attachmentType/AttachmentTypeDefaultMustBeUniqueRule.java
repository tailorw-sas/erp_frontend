package com.kynsoft.finamer.payment.domain.rules.attachmentType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;

import java.util.UUID;

public class AttachmentTypeDefaultMustBeUniqueRule extends BusinessRule {

    private final IManageAttachmentTypeService service;

    private final UUID id;

    public AttachmentTypeDefaultMustBeUniqueRule(IManageAttachmentTypeService service, UUID id) {
        super(
                DomainErrorMessage.ATTACHMENT_TYPE_CHECK_DEFAULT,
                new ErrorField("defaults", DomainErrorMessage.ATTACHMENT_TYPE_CHECK_DEFAULT.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByDefaultAndNotId(id) > 0;
    }

}
