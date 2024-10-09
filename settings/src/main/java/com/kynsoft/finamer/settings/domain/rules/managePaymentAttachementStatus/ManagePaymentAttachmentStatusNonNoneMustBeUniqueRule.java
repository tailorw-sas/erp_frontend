package com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentAttachmentStatusService;

import java.util.UUID;

public class ManagePaymentAttachmentStatusNonNoneMustBeUniqueRule extends BusinessRule {

    private final IManagePaymentAttachmentStatusService service;

    private final UUID id;

    public ManagePaymentAttachmentStatusNonNoneMustBeUniqueRule(IManagePaymentAttachmentStatusService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_NONNONE,
                new ErrorField("nonNone", DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_NONNONE.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByNonNoneAndNotId(id) > 0;
    }

}
