package com.kynsoft.finamer.settings.domain.rules.managePaymentSource;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManagePaymentSourceNameMustBeNullRule extends BusinessRule {
    private final String name;

    public ManagePaymentSourceNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.MANAGE_PAYMENT_SOURCE_NAME_CANNOT_BE_EMPTY,
                new ErrorField("name", "The name of the payment source cannot be empty.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }
}
