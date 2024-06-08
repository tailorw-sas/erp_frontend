package com.kynsof.identity.domain.rules.module;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ModuleDescriptionMustBeNullRule extends BusinessRule {

    private final String description;

    public ModuleDescriptionMustBeNullRule(String description) {
        super(
                DomainErrorMessage.MODULE_DESCRIPTION_CANNOT_BE_EMPTY, 
                new ErrorField("description", "The description of the module cannot be empty.")
        );
        this.description = description;
    }

    @Override
    public boolean isBroken() {
        return this.description == null || this.description.isEmpty();
    }

}
