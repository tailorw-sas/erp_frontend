package com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentAttachmentStatusService;

import java.util.UUID;

public class ManagePaymentAttachmentStatusWhitOutAttachmentMustBeUniqueRule extends BusinessRule {

    private final IManagePaymentAttachmentStatusService service;

    private final UUID id;

    public ManagePaymentAttachmentStatusWhitOutAttachmentMustBeUniqueRule(IManagePaymentAttachmentStatusService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_PWA_WHIT_OUT_ATTACHMENT,
                new ErrorField("pwaWithOutAttachment", DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_PWA_WHIT_OUT_ATTACHMENT.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByPwaWithOutAttachmentAndNotId(id) > 0;
    }

}
