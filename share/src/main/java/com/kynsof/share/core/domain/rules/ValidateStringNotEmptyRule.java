package com.kynsof.share.core.domain.rules;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;

public class ValidateStringNotEmptyRule extends BusinessRule{

    private final String string;

    public ValidateStringNotEmptyRule(String toValidate, String objectName, String message){
        super(DomainErrorMessage.STRING_MUST_NOT_BE_EMPTY, new ErrorField(objectName, message));
        this.string = toValidate;
    }

    @Override
    public boolean isBroken() {
        return this.string.isEmpty();
    }
}
