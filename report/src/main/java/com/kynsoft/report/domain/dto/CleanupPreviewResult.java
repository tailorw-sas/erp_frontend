package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CleanupPreviewResult {
    private LocalDate targetDate;
    private int totalFilesFound;
    private long totalSizeBytes;
    private List<String> filePaths;
}

