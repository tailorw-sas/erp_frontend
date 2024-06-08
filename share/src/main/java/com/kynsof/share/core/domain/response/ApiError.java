package com.kynsof.share.core.domain.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiError {
    private int status;
    private String errorMessage;
    private List<ErrorField> errors;

    public ApiError(int status, String errorMessage, List<ErrorField> errors) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.errors = errors;
    }

    public ApiError() {
    }

    public ApiError(int status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public ApiError(String errorMessage, List<ErrorField> errors) {
        this.errorMessage = errorMessage;
        this.errors = errors;
    }

    public ApiError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static ApiError withSingleError(String errorMessage, String field, String message) {
        ApiError apiError = new ApiError(errorMessage);
        ErrorField error = new ErrorField(field, message);
        apiError.setErrors(java.util.Collections.singletonList(error));
        return apiError;
    }
}