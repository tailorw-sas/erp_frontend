package com.kynsoft.finamer.settings.domain.rules.manageAttachmentType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageAttachmentTypeService;

import java.util.UUID;

public class ManageAttachmentTypeDefaultMustBeUniqueRule extends BusinessRule {

    private final IManageAttachmentTypeService service;

    private final UUID id;

    public ManageAttachmentTypeDefaultMustBeUniqueRule(IManageAttachmentTypeService service, UUID id) {
        super(
                DomainErrorMessage.INCOME_ATTACHMENT_TYPE_CHECK_DEFAULT,
                new ErrorField("defaults", DomainErrorMessage.INCOME_ATTACHMENT_TYPE_CHECK_DEFAULT.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByDefaultAndNotId(id) > 0;
    }

}
