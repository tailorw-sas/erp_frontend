package com.kynsoft.finamer.invoicing.domain.rules.manageInvoiceStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;

import java.util.UUID;

public class ManageInvoiceStatusCodeMustBeUniqueRule extends BusinessRule {
    private final IManageInvoiceStatusService service;

    private final String code;

    private final UUID id;

    public ManageInvoiceStatusCodeMustBeUniqueRule(IManageInvoiceStatusService service, String code, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("code", DomainErrorMessage.ITEM_ALREADY_EXITS.getReasonPhrase())
        );
        this.service = service;
        this.code = code;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCodeAndNotId(code, id) > 0;
    }
}
