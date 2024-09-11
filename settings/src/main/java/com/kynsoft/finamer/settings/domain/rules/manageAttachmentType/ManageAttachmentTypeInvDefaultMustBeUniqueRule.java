package com.kynsoft.finamer.settings.domain.rules.manageAttachmentType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageAttachmentTypeService;

import java.util.UUID;

public class ManageAttachmentTypeInvDefaultMustBeUniqueRule extends BusinessRule {

    private final IManageAttachmentTypeService service;

    private final UUID id;

    public ManageAttachmentTypeInvDefaultMustBeUniqueRule(IManageAttachmentTypeService service, UUID id) {
        super(
                DomainErrorMessage.ATTACHMENT_TYPE_CHECK_INV_DEFAULT,
                new ErrorField("defaults", DomainErrorMessage.ATTACHMENT_TYPE_CHECK_INV_DEFAULT.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByAttachInvDefaultAndNotId(id) > 0;
    }

}
