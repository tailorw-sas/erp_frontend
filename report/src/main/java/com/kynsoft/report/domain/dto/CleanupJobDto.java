package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CleanupJobDto {
    private String jobId;
    private LocalDate cleanupDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private CleanupResult result;
    private String status; // "RUNNING", "COMPLETED", "FAILED"
}