package com.kynsoft.finamer.invoicing.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración específica para el procesamiento de room rates
 */
@Configuration
public class RoomRateProcessingConfig {

    /**
     * Configuración de tamaños de lote para diferentes operaciones
     */
    @Bean
    public BatchSizeConfig batchSizeConfig() {
        return BatchSizeConfig.builder()
                .roomRateValidationBatchSize(100)
                .roomRateProcessingBatchSize(50)
                .bookingCreationBatchSize(25)
                .invoiceCreationBatchSize(10)
                .maxConcurrentBatches(4)
                .build();
    }

    /**
     * Configuración de timeouts para diferentes operaciones
     */
    @Bean
    public ProcessingTimeouts processingTimeouts() {
        return ProcessingTimeouts.builder()
                .excelReadTimeoutSeconds(30)
                .validationTimeoutSeconds(120)
                .batchProcessingTimeoutSeconds(60)
                .overallImportTimeoutMinutes(30)
                .build();
    }

    @lombok.Data
    @lombok.Builder
    public static class BatchSizeConfig {
        private int roomRateValidationBatchSize;
        private int roomRateProcessingBatchSize;
        private int bookingCreationBatchSize;
        private int invoiceCreationBatchSize;
        private int maxConcurrentBatches;

        /**
         * Ajusta el tamaño de lote basado en la cantidad total de elementos
         */
        public int getAdjustedBatchSize(int baseSize, int totalElements) {
            if (totalElements < 100) {
                return Math.min(baseSize / 2, totalElements);
            } else if (totalElements > 1000) {
                return Math.min(baseSize * 2, 200);
            }
            return baseSize;
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class ProcessingTimeouts {
        private int excelReadTimeoutSeconds;
        private int validationTimeoutSeconds;
        private int batchProcessingTimeoutSeconds;
        private int overallImportTimeoutMinutes;

        /**
         * Calcula timeout dinámico basado en la cantidad de elementos
         */
        public int calculateDynamicTimeout(int baseTimeoutSeconds, int elementCount) {
            // Agregar 1 segundo por cada 100 elementos adicionales
            int additionalTime = (elementCount / 100) * 1;
            return baseTimeoutSeconds + additionalTime;
        }
    }
}
