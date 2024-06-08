package com.kynsoft.finamer.settings.domain.rules.managerCurrency;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManagerCurrencyNameMustBeNullRule extends BusinessRule {

    private final String name;

    public ManagerCurrencyNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.MANAGER_CURRENCY_NAME_CANNOT_BE_EMPTY, 
                new ErrorField("name", "The name of the currency cannot be empty.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }

}
