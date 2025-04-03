package com.kynsof.share.core.domain.response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadFileResponse extends InternalResponse {

    public UploadFileResponse(ResponseStatus status) {
        super(status);
    }

    public UploadFileResponse(ResponseStatus status, String message) {
        super(status, message);
    }
}
