package com.kynsoft.finamer.invoicing.domain.rules.manageInvoiceStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageInvoiceStatusNameMustBeNullRule extends BusinessRule {

    private final String name;

    public ManageInvoiceStatusNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.MANAGE_INVOICE_STATUS_NAME_CANNOT_BE_EMPTY,
                new ErrorField("name", "The name cannot be empty.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }
}
