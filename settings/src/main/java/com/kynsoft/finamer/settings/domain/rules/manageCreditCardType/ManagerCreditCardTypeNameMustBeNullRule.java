package com.kynsoft.finamer.settings.domain.rules.manageCreditCardType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManagerCreditCardTypeNameMustBeNullRule extends BusinessRule {

    private final String name;

    public ManagerCreditCardTypeNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.MANAGER_CREDIT_CARD_TYPE_NAME_CANNOT_BE_EMPTY, 
                new ErrorField("name", "The name cannot be empty.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }

}
