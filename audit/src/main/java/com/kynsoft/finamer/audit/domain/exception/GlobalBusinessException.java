package com.kynsoft.finamer.audit.domain.exception;


import com.kynsoft.finamer.audit.domain.response.ErrorField;

public class GlobalBusinessException {

    private final DomainErrorMessage error;

    private final ErrorField errorField;

    public GlobalBusinessException(DomainErrorMessage error, ErrorField errorField) {
        this.error = error;
        this.errorField = errorField;
    }

    public DomainErrorMessage getError() {
        return error;
    }

    public ErrorField getErrorField() {
        return errorField;
    }

}
