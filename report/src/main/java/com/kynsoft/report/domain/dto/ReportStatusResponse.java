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
public class ReportStatusResponse {
    private String requestId;        // ID del servidor
    private String clientRequestId;  // ID del cliente
    private String jasperReportCode;
    private ReportStatus status;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String reportFormatType;
    private Long fileSizeBytes;
}