package com.kynsoft.finamer.settings.domain.rules.manageInvoiceTransactionType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTransactionTypeService;

import java.util.UUID;

public class ManageInvoiceTransactionTypeDefaultMustBeUniqueRule extends BusinessRule {

    private final IManageInvoiceTransactionTypeService service;

    private final UUID id;

    public ManageInvoiceTransactionTypeDefaultMustBeUniqueRule(IManageInvoiceTransactionTypeService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_AGENCY_CHECK_DEFAULT,
                new ErrorField("default", DomainErrorMessage.MANAGE_AGENCY_CHECK_DEFAULT.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByDefaultsAndNotId(id) > 0;
    }

}
