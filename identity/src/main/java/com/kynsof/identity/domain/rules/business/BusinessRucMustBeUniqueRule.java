package com.kynsof.identity.domain.rules.business;

import com.kynsof.identity.domain.interfaces.service.IBusinessService;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.UUID;

public class BusinessRucMustBeUniqueRule extends BusinessRule {

    private final IBusinessService service;

    private final String ruc;

    private final UUID id;

    public BusinessRucMustBeUniqueRule(IBusinessService service, String ruc, UUID id) {
        super(
                DomainErrorMessage.BUSINESS_RUC_MUST_BY_UNIQUE, 
                new ErrorField("ruc", "The business ruc must be unique.")
        );
        this.service = service;
        this.ruc = ruc;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByRucAndNotId(ruc, id) > 0;
    }

}
