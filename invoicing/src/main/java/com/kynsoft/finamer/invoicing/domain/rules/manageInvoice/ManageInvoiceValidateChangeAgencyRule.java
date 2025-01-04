package com.kynsoft.finamer.invoicing.domain.rules.manageInvoice;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;

public class ManageInvoiceValidateChangeAgencyRule extends BusinessRule {

    private final EInvoiceStatus status;

    public ManageInvoiceValidateChangeAgencyRule(EInvoiceStatus status) {
        super(
                DomainErrorMessage.MANAGE_INVOICE_CHANGE_AGENCY, 
                new ErrorField("agency", DomainErrorMessage.MANAGE_INVOICE_CHANGE_AGENCY.getReasonPhrase())
        );
        this.status = status;
    }

    @Override
    public boolean isBroken() {
        return !this.status.equals(EInvoiceStatus.PROCECSED) && !this.status.equals(EInvoiceStatus.SENT) && !this.status.equals(EInvoiceStatus.RECONCILED);
    }

}
