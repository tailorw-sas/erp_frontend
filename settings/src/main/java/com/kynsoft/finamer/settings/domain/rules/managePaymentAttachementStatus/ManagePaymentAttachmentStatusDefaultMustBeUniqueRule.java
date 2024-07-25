package com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentAttachmentStatusService;

import java.util.UUID;

public class ManagePaymentAttachmentStatusDefaultMustBeUniqueRule extends BusinessRule {

    private final IManagePaymentAttachmentStatusService service;

    private final UUID id;

    public ManagePaymentAttachmentStatusDefaultMustBeUniqueRule(IManagePaymentAttachmentStatusService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_DEFAULT,
                new ErrorField("code", DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_DEFAULT.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByDefaultAndNotId(id) > 0;
    }

}
