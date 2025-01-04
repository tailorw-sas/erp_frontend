package com.kynsoft.finamer.invoicing.domain.rules.manageInvoiceStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;

import java.util.UUID;

public class ManageInvoiceStatusReconciledMustBeUniqueRule extends BusinessRule {

    private final IManageInvoiceStatusService service;

    private final UUID id;

    public ManageInvoiceStatusReconciledMustBeUniqueRule(IManageInvoiceStatusService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_INVOICE_STATUS_CHECK_RECONCILED,
                new ErrorField("reconciledStatus", DomainErrorMessage.MANAGE_INVOICE_STATUS_CHECK_RECONCILED.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByReconciledStatusAndNotId(id) > 0;
    }

}
