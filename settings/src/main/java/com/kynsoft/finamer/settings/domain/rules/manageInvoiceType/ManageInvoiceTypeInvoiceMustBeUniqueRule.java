package com.kynsoft.finamer.settings.domain.rules.manageInvoiceType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTypeService;

import java.util.UUID;

public class ManageInvoiceTypeInvoiceMustBeUniqueRule extends BusinessRule {
    private final IManageInvoiceTypeService service;

    private final UUID id;

    public ManageInvoiceTypeInvoiceMustBeUniqueRule(IManageInvoiceTypeService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_TRANSACTION_TYPE_CHECK_INVOICE,
                new ErrorField("invoice", DomainErrorMessage.MANAGE_TRANSACTION_TYPE_CHECK_INVOICE.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByInvoiceAndNotId(id) > 0;
    }
}
