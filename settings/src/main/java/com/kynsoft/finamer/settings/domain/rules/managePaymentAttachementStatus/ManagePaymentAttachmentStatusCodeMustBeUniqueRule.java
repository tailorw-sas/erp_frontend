package com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentAttachmentStatusService;

import java.util.UUID;

public class ManagePaymentAttachmentStatusCodeMustBeUniqueRule extends BusinessRule {
    private final IManagePaymentAttachmentStatusService service;
    private final String code;
    private final UUID id;

    public ManagePaymentAttachmentStatusCodeMustBeUniqueRule(final IManagePaymentAttachmentStatusService service, final String code, final UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("code", DomainErrorMessage.ITEM_ALREADY_EXITS.toString())
        );
        this.service = service;
        this.code = code;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return service.countByCode(code, id) > 0;
    }

}
