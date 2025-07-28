package com.kynsoft.finamer.invoicing.domain.dto.importresult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Estado del proceso de importación para tracking
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportProgress {
    private String importProcessId;
    private ImportStatus status;
    private int totalItems;
    private int processedItems;
    private int currentPhase;
    private int totalPhases;
    private String currentPhaseDescription;
    private LocalDateTime startTime;
    private LocalDateTime estimatedCompletionTime;
    private List<String> recentLogs;

    public enum ImportStatus {
        PENDING,
        READING_DATA,
        VALIDATING,
        PROCESSING,
        CREATING_BOOKINGS,
        CREATING_INVOICES,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    /**
     * Calcula el porcentaje de progreso general
     */
    public double getOverallProgress() {
        if (totalItems == 0) return 0;

        double itemProgress = (double) processedItems / totalItems;
        double phaseProgress = (double) currentPhase / totalPhases;

        // Combinar progreso de items y fases
        return (itemProgress * 0.8 + phaseProgress * 0.2) * 100;
    }

    /**
     * Indica si el proceso está activo
     */
    public boolean isActive() {
        return status != ImportStatus.COMPLETED &&
                status != ImportStatus.FAILED &&
                status != ImportStatus.CANCELLED;
    }
}
