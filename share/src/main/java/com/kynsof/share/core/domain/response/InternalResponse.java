package com.kynsof.share.core.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class InternalResponse {
    protected ResponseStatus status;
    protected String message;

    protected InternalResponse(ResponseStatus status) {
        this.status = status;
        message = "";
    }
    protected InternalResponse(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
