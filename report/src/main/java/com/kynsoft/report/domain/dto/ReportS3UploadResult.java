package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportS3UploadResult {
    private boolean success;
    private String s3ObjectKey;
    private String preSignedUrl;
    private LocalDateTime expirationDate;
    private String errorMessage;
    private int attemptNumber;
}
