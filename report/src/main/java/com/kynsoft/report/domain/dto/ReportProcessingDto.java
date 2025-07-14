package com.kynsoft.report.domain.dto;
import com.kynsoft.report.domain.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportProcessingDto {
    private String serverRequestId;
    private String clientRequestId;
    private String jasperReportCode;
    private ReportStatus status;
    private String errorMessage;
    private String reportBase64;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String reportFormatType;
    private Long fileSizeBytes;
    private String originalRequestJson;  // Para auditoría completa

    private boolean useS3Storage;
    private String s3BucketName;
    private String s3ObjectKey;
    private String s3PreSignedUrl;
    private LocalDateTime s3ExpirationDate;
    private String storageMethod; // "S3" o "BASE64"
}
