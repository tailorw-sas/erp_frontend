package com.kynsoft.finamer.invoicing.domain.dto.importresult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportStats {
    private long totalProcessingTimeMs;
    private long validationTimeMs;
    private long dataLoadingTimeMs;
    private long bookingCreationTimeMs;
    private long invoiceCreationTimeMs;

    // Estadísticas de throughput
    private double roomRatesPerSecond;
    private double bookingsPerSecond;
    private double invoicesPerSecond;

    // Uso de recursos
    private int maxConcurrentBatches;
    private int totalBatches;
    private long avgBatchSizeBytes;

    /**
     * Calcula el throughput basado en el tiempo total y cantidad procesada
     */
    public static ImportStats calculate(int totalProcessed, int bookings, int invoices,
                                        long totalTimeMs, long validationTimeMs) {
        double seconds = totalTimeMs / 1000.0;

        return ImportStats.builder()
                .totalProcessingTimeMs(totalTimeMs)
                .validationTimeMs(validationTimeMs)
                .roomRatesPerSecond(seconds > 0 ? totalProcessed / seconds : 0)
                .bookingsPerSecond(seconds > 0 ? bookings / seconds : 0)
                .invoicesPerSecond(seconds > 0 ? invoices / seconds : 0)
                .build();
    }
}
