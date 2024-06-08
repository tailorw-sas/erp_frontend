package com.kynsof.share.core.domain.exception;

import com.kynsof.share.core.domain.response.ApiError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final ApiError apiError;
    private final HttpStatus status;

    public CustomException(ApiError apiError, HttpStatus status) {
        super(apiError.getErrorMessage());
        this.apiError = apiError;
        this.status = status;
    }

}
