package com.kynsoft.finamer.invoicing.domain.rules.manageInvoice;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.invoicing.domain.dto.ManageClientDto;

public class InvoiceValidateClienteRule extends BusinessRule {

    private final ManageClientDto clientDto;

    public InvoiceValidateClienteRule(ManageClientDto clientDto) {
        super(
                DomainErrorMessage.INVOICE_AGENCY_WITH_CLIENT_INACTIVE, 
                new ErrorField("agency", DomainErrorMessage.INVOICE_AGENCY_WITH_CLIENT_INACTIVE.getReasonPhrase())
        );
        this.clientDto = clientDto;
    }

    @Override
    public boolean isBroken() {
        return this.clientDto.getStatus().equals("INACTIVE");
    }

}
