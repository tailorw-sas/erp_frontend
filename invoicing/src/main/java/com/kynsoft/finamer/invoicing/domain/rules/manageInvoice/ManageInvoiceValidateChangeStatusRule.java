package com.kynsoft.finamer.invoicing.domain.rules.manageInvoice;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;

public class ManageInvoiceValidateChangeStatusRule extends BusinessRule {

    private final EInvoiceStatus status;

    public ManageInvoiceValidateChangeStatusRule(EInvoiceStatus status) {
        super(
                DomainErrorMessage.MANAGE_INVOICE_CHANGE_STATUS, 
                new ErrorField("status", DomainErrorMessage.MANAGE_INVOICE_CHANGE_STATUS.getReasonPhrase())
        );
        this.status = status;
    }

    @Override
    public boolean isBroken() {
        return !this.status.equals(EInvoiceStatus.PROCECSED);
    }

}
