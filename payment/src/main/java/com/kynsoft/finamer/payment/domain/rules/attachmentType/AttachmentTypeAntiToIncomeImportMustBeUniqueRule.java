package com.kynsoft.finamer.payment.domain.rules.attachmentType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;

import java.util.UUID;

public class AttachmentTypeAntiToIncomeImportMustBeUniqueRule extends BusinessRule {

    private final IManageAttachmentTypeService service;

    private final UUID id;

    public AttachmentTypeAntiToIncomeImportMustBeUniqueRule(IManageAttachmentTypeService service, UUID id) {
        super(
                DomainErrorMessage.ATTACHMENT_TYPE_CHECK_ANTI,
                new ErrorField("antiToIncomeImport", DomainErrorMessage.ATTACHMENT_TYPE_CHECK_ANTI.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByAntiToIncomeImportAndNotId(id) > 0;
    }

}
