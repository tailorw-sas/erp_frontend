package com.kynsof.share.core.domain.rules;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;

public class ValidateObjectNotNullRule<T> extends BusinessRule {

    private final T object;

    public ValidateObjectNotNullRule(T objectValidate, String objectName, String msg) {
        super(DomainErrorMessage.OBJECT_NOT_NULL, new ErrorField(objectName, msg));
        this.object = objectValidate;
    }

    @Override
    public boolean isBroken() {
        return this.object == null;
    }

}
