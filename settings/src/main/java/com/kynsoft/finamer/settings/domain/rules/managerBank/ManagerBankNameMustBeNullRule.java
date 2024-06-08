package com.kynsoft.finamer.settings.domain.rules.managerBank;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManagerBankNameMustBeNullRule extends BusinessRule {

    private final String name;

    public ManagerBankNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.MANAGER_BANK_NAME_CANNOT_BE_EMPTY, 
                new ErrorField("name", "The name of the Bank cannot be empty.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }

}
