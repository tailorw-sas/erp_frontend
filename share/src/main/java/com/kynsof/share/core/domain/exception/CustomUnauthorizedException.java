package com.kynsof.share.core.domain.exception;

import com.kynsof.share.core.domain.response.ErrorField;

public class CustomUnauthorizedException extends RuntimeException  {
    private final ErrorField errorField;

    public CustomUnauthorizedException(String message, ErrorField errorField) {
        super(message);
        this.errorField = errorField;
    }
}
