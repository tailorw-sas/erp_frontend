package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CleanupResult {
    private LocalDate cleanupDate;
    private int totalFilesFound;
    private int filesDeletedSuccessfully;
    private int filesFailed;
    private List<String> failedFiles;
    private long totalSizeBytes;
    private Duration executionTime;
    private boolean success;
    private String errorMessage;
}
