package com.kynsof.identity.domain.rules.module;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ModuleNameMustBeNullRule extends BusinessRule {

    private final String name;

    public ModuleNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.MODULE_NAME_CANNOT_BE_EMPTY, 
                new ErrorField("name", "The name of the module cannot be empty.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }

}
