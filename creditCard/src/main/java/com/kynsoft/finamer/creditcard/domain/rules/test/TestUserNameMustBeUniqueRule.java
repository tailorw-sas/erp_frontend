package com.kynsoft.finamer.creditcard.domain.rules.test;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.creditcard.domain.services.ITestService;

import java.util.UUID;

public class TestUserNameMustBeUniqueRule extends BusinessRule {

    private final ITestService service;

    private final String name;

    private final UUID id;

    public TestUserNameMustBeUniqueRule(ITestService service, String name, UUID id) {
        super(
                DomainErrorMessage.MODULE_NAME_MUST_BY_UNIQUE, 
                new ErrorField("userName", "The Test username must be unique.")
        );
        this.service = service;
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByNameAndNotId(name, id) > 0;
    }

}
