package com.kynsoft.finamer.audit.domain.rules;


import com.kynsoft.finamer.audit.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.audit.domain.response.ErrorField;

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
