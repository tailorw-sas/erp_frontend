package com.kynsoft.finamer.invoicing.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Specific configuration for processing room rates
 */
@Configuration
public class RoomRateProcessingConfig {

    /**
     * Setting lot sizes for different operations
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
     * Setting timeouts for different operations
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
         * Adjusts the batch size based on the total number of items
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
         * Calculates dynamic timeout based on the number of elements
         */
        public int calculateDynamicTimeout(int baseTimeoutSeconds, int elementCount) {
            // Add 1 second for every 100 additional items
            int additionalTime = (elementCount / 100) * 1;
            return baseTimeoutSeconds + additionalTime;
        }
    }
}
