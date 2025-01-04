package com.kynsoft.finamer.audit.domain.exception;


import com.kynsoft.finamer.audit.domain.response.ErrorField;

public class CustomUnauthorizedException extends RuntimeException  {
    private final ErrorField errorField;

    public CustomUnauthorizedException(String message, ErrorField errorField) {
        super(message);
        this.errorField = errorField;
    }
}
