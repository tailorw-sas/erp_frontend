package com.kynsoft.finamer.settings.domain.rules.managePaymentAttachementStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentAttachmentStatusService;

import java.util.UUID;

public class ManagePaymentAttachmentStatusNameMustBeUniqueRule extends BusinessRule {
    private final IManagePaymentAttachmentStatusService service;
    private final String name;
    private final UUID id;

    public ManagePaymentAttachmentStatusNameMustBeUniqueRule(final IManagePaymentAttachmentStatusService service, final String name, final UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("name", DomainErrorMessage.ITEM_ALREADY_EXITS.getReasonPhrase())
        );
        this.service = service;
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return service.countByNameAndNotId(name, id) > 0;
    }

}
