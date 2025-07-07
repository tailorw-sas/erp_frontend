package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String details;
    private String code;
    private LocalDateTime timestamp;
    private List<String> validationErrors;

    public ErrorResponse(String error, String details) {
        this.error = error;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}