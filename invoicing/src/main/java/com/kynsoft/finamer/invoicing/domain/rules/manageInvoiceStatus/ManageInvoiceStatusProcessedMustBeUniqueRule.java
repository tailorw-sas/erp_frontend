package com.kynsoft.finamer.invoicing.domain.rules.manageInvoiceStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;

import java.util.UUID;

public class ManageInvoiceStatusProcessedMustBeUniqueRule extends BusinessRule {

    private final IManageInvoiceStatusService service;

    private final UUID id;

    public ManageInvoiceStatusProcessedMustBeUniqueRule(IManageInvoiceStatusService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_INVOICE_STATUS_CHECK_PROCESSED,
                new ErrorField("processStatus", DomainErrorMessage.MANAGE_INVOICE_STATUS_CHECK_PROCESSED.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByProcessStatusAndNotId(id) > 0;
    }

}
