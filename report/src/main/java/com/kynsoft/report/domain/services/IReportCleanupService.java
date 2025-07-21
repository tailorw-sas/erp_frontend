package com.kynsoft.report.domain.services;

import com.kynsoft.report.domain.dto.CleanupPreviewResult;
import com.kynsoft.report.domain.dto.CleanupResult;

import java.time.LocalDate;

public interface IReportCleanupService {

    /**
     * Ejecutar cleanup automático diario
     */
    void executeScheduledCleanup();

    /**
     * Cleanup manual de fecha específica
     */
    CleanupResult cleanupReportsForDate(LocalDate date);

    /**
     * Cleanup manual de rango de fechas
     */
    CleanupResult cleanupReportsForDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Verificar qué archivos serían eliminados (dry run)
     */
    CleanupPreviewResult previewCleanup(LocalDate date);
}