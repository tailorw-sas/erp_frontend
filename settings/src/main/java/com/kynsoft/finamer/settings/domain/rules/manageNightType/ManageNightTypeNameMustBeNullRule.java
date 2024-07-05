package com.kynsoft.finamer.settings.domain.rules.manageNightType;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageNightTypeNameMustBeNullRule extends BusinessRule {

    private final String name;

    public ManageNightTypeNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.FIELD_IS_REQUIRED, 
                new ErrorField("name", "The field is required.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }

}
