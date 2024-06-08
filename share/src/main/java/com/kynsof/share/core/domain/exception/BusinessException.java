package com.kynsof.share.core.domain.exception;


public class BusinessException extends RuntimeException {

    private final int status;

    private final String message;

    private final String details;

    public BusinessException(DomainErrorMessage status, String details) {
        super(status.getReasonPhrase());
        this.status = status.value();
        this.message = status.getReasonPhrase();
        this.details = details;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

}
