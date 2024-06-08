package com.kynsoft.finamer.creditcard.domain.rules.test;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class TestUserNameMustBeNullRule extends BusinessRule {

    private final String userName;

    public TestUserNameMustBeNullRule(String userName) {
        super(
                DomainErrorMessage.MODULE_NAME_CANNOT_BE_EMPTY, 
                new ErrorField("userName", "The username of the module cannot be empty.")
        );
        this.userName = userName;
    }

    @Override
    public boolean isBroken() {
        return this.userName == null || this.userName.isEmpty();
    }

}
