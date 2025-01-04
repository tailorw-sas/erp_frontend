package com.kynsof.share.core.domain.exception;

import com.kynsof.share.core.domain.response.ErrorField;
import lombok.Getter;

@Getter
public class ExcelException extends RuntimeException {
    private final String domainErrorMessage;

    public ExcelException(String domainErrorMessage) {
        super(domainErrorMessage);
        this.domainErrorMessage = domainErrorMessage;
    }
}
