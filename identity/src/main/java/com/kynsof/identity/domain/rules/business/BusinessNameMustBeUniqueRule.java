package com.kynsof.identity.domain.rules.business;

import com.kynsof.identity.domain.interfaces.service.IBusinessService;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.UUID;

public class BusinessNameMustBeUniqueRule extends BusinessRule {

    private final IBusinessService service;

    private final String name;

    private final UUID id;

    public BusinessNameMustBeUniqueRule(IBusinessService service, String name, UUID id) {
        super(
                DomainErrorMessage.BUSINESS_NAME_MUST_BY_UNIQUE, 
                new ErrorField("name", "The business name must be unique.")
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
