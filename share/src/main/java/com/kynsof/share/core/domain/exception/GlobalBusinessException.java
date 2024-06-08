package com.kynsof.share.core.domain.exception;

import com.kynsof.share.core.domain.response.ErrorField;

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
