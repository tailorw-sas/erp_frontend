package com.kynsoft.finamer.settings.domain.rules.manageInvoiceType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageInvoiceTypeNameMustBeNullRule extends BusinessRule {

    private final String name;

    public ManageInvoiceTypeNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.MANAGE_INVOICE_TYPE_NAME_CANNOT_BE_EMPTY,
                new ErrorField("name", "The name of the invoice type cannot be empty.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }
}
