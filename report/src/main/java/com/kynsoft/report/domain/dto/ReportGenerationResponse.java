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
public class ReportGenerationResponse {
    private boolean useS3Storage;
    private String base64Report;          // Solo si S3 falla
    private String s3DownloadUrl;         // Solo si S3 funciona
    private String fileName;
    private String contentType;
    private Long fileSizeBytes;
    private LocalDateTime expirationDate;
    private String storageMethod;         // "S3" o "BASE64"
    private String errorMessage;          // Si hay errores parciales
}
