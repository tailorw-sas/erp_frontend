package com.kynsof.share.core.domain.exception;

public class BusinessNotFoundException extends RuntimeException {

    private final GlobalBusinessException brokenRule;

    private final int status;

    private final String message;

    public BusinessNotFoundException(GlobalBusinessException brokenRule) {
        super(brokenRule.getError().getReasonPhrase());
        this.status = brokenRule.getError().value();
        this.message = brokenRule.getError().getReasonPhrase();
        this.brokenRule = brokenRule;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public GlobalBusinessException getBrokenRule() {
        return brokenRule;
    }

}
