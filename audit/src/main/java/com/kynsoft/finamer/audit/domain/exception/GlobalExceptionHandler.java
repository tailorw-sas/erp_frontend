package com.kynsoft.finamer.audit.domain.exception;

import com.kynsoft.finamer.audit.domain.response.ApiError;
import com.kynsoft.finamer.audit.domain.response.ApiResponse;
import com.kynsoft.finamer.audit.domain.response.ErrorField;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        ApiError apiError = new ApiError(ex.getMessage(), null); // Assuming constructor ApiError(message, errors)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail(apiError));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        List<ErrorField> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorField(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        ApiError apiError = new ApiError("Validation Error", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(apiError));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<?>> handleAllUncaughtException(Exception ex) {
        ApiError apiError = new ApiError("An unexpected error occurred.",
                List.of(new ErrorField("internal", "Internal server error.")));
        return ResponseEntity.internalServerError().body(ApiResponse.fail(apiError));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleAlreadyExistsException(AlreadyExistsException ex) {
        ApiError apiError = new ApiError( ex.getMessage(),
                List.of(ex.getErrorField()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.fail(apiError));
    }

    @ExceptionHandler(BusinessRuleValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessRuleValidationException(BusinessRuleValidationException ex) {
        ApiError apiError = new ApiError(ex.getStatus(), ex.getMessage(),
                List.of(ex.getBrokenRule().getErrorField()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(apiError));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        ApiError apiError = new ApiError(ex.getStatus(), ex.getDetails());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(apiError));
    }

    @ExceptionHandler(BusinessNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessNotFoundException ex) {
        ApiError apiError = new ApiError(ex.getStatus(), ex.getMessage(),
                List.of(ex.getBrokenRule().getErrorField()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(apiError));
    }



    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<?>> handleNullPointerException(NullPointerException ex) {
        ApiError apiError = new ApiError("An unexpected null value was encountered.", null);
        ApiResponse<?> apiResponse = ApiResponse.fail(apiError);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpClientErrorException(HttpClientErrorException ex) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        ApiError apiError = new ApiError("HTTP Error: " + ex.getMessage(), null);
        ApiResponse<?> apiResponse = ApiResponse.fail(apiError);
        return new ResponseEntity<>(apiResponse, status);
    }

    @ExceptionHandler(CustomUnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomUnauthorizedException(CustomUnauthorizedException ex) {
        ApiError apiError = new ApiError("An unexpected null value was encountered.", null);
        ApiResponse<?> apiResponse = ApiResponse.fail(apiError);
        return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
    }

}
