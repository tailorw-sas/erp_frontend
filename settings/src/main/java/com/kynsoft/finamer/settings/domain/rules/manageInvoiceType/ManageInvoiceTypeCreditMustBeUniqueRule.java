package com.kynsoft.finamer.settings.domain.rules.manageInvoiceType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTypeService;

import java.util.UUID;

public class ManageInvoiceTypeCreditMustBeUniqueRule extends BusinessRule {
    private final IManageInvoiceTypeService service;

    private final UUID id;

    public ManageInvoiceTypeCreditMustBeUniqueRule(IManageInvoiceTypeService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_TRANSACTION_TYPE_CHECK_CREDIT,
                new ErrorField("credit", DomainErrorMessage.MANAGE_TRANSACTION_TYPE_CHECK_CREDIT.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCreditAndNotId(id) > 0;
    }
}
