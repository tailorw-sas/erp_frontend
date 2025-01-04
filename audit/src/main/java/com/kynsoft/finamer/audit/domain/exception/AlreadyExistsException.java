package com.kynsoft.finamer.audit.domain.exception;


import com.kynsoft.finamer.audit.domain.response.ErrorField;
import lombok.Getter;

@Getter
public class AlreadyExistsException extends RuntimeException {
    private final ErrorField errorField;

    public AlreadyExistsException(String message, ErrorField errorField) {
        super(message);
        this.errorField = errorField;
    }

}
