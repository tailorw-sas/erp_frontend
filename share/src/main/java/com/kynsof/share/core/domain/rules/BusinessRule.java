package com.kynsof.share.core.domain.rules;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;

public abstract class BusinessRule {

    private final DomainErrorMessage error;

    private final ErrorField errorField;

    protected BusinessRule(DomainErrorMessage error, ErrorField errorField) {
        this.error = error;
        this.errorField = errorField;
    }

    public abstract boolean isBroken();

    public DomainErrorMessage getError() {
        return error;
    }

    public ErrorField getErrorField() {
        return errorField;
    }

}
