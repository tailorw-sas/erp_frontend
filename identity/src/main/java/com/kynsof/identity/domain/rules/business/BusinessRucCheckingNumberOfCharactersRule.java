package com.kynsof.identity.domain.rules.business;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class BusinessRucCheckingNumberOfCharactersRule extends BusinessRule {

    private final String business_ruc;

    public BusinessRucCheckingNumberOfCharactersRule(String business_ruc) {
        super(
                DomainErrorMessage.BUSINESS_RUC, 
                new ErrorField("ruc", "The business's RUC must have thirteen characters.")
        );
        this.business_ruc = business_ruc;
    }

    @Override
    public boolean isBroken() {
        return business_ruc.length() != 13;
    }

}
