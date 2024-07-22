package com.kynsof.share.core.domain.exception;

import com.kynsof.share.core.domain.response.ErrorField;
import lombok.Getter;

@Getter
public class ReadExcelException extends RuntimeException {
    private final DomainErrorMessage domainErrorMessage;
    private final ErrorField errorField;

    public ReadExcelException(DomainErrorMessage domainErrorMessage, ErrorField errorField) {
        super(domainErrorMessage.getReasonPhrase());
        this.domainErrorMessage = domainErrorMessage;
        this.errorField = errorField;
    }
}
