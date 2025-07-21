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
public class ReportDownloadResponse {
    private String base64Report;
    private Long fileSizeBytes;
    private String fileName;
    private String contentType;
    private String downloadUrl; // Para descargas por URL si el archivo es muy grande
    private String storageMethod;
    private LocalDateTime expirationDate;
}
