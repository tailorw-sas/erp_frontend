package com.kynsoft.finamer.invoicing.domain.rules.manageInvoiceStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;

import java.util.UUID;

public class ManageInvoiceStatusSentMustBeUniqueRule extends BusinessRule {

    private final IManageInvoiceStatusService service;

    private final UUID id;

    public ManageInvoiceStatusSentMustBeUniqueRule(IManageInvoiceStatusService service, UUID id) {
        super(
                DomainErrorMessage.MANAGE_INVOICE_STATUS_CHECK_SENT,
                new ErrorField("sentStatus", DomainErrorMessage.MANAGE_INVOICE_STATUS_CHECK_SENT.getReasonPhrase())
        );
        this.service = service;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        Long cant = this.service.countBySentStatusAndNotId(id);
        System.err.println("#################################");
        System.err.println("#################################");
        System.err.println("#################################");
        System.err.println("################################# " + id);
        System.err.println("#################################" + cant);
        System.err.println("#################################");
        System.err.println("#################################");
        
        return cant > 0;
    }

}
