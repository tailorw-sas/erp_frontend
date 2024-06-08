package com.kynsoft.finamer.settings.domain.rules.managerB2BPartnerType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageB2BPartnerTypeNameMustBeNullRule extends BusinessRule {

    private final String name;

    public ManageB2BPartnerTypeNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.NAME_CANNOT_BE_EMPTY, 
                new ErrorField("name", "The name cannot be empty.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }

}
