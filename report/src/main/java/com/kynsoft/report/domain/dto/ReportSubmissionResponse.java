package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSubmissionResponse {
    private String requestId;        // ID del servidor (para consultas)
    private String clientRequestId;  // ID del cliente (para referencia)
    private String message;
    private String status;
    private Long timestamp;
}
