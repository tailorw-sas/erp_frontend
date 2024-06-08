package com.kynsoft.finamer.settings.domain.rules.manageEmployeeGroup;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageEmployeeGroupNameMustNotBeNullRule extends BusinessRule {

    private final String name;

    public ManageEmployeeGroupNameMustNotBeNullRule(String name) {
        super(
                DomainErrorMessage.MANAGE_EMPLOYEE_GROUP_NAME_CANNOT_BE_EMPTY,
                new ErrorField("name", "The name cannot be empty.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }

}
