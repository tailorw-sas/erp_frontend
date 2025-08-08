package com.kynsoft.finamer.invoicing.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Infrastructure configuration for import processing
 * Optimized to handle high volume room rates with controlled parallelization
 */
@Configuration
@EnableAsync
@EnableTransactionManagement
public class ImportProcessingConfig {

    /**
     * Executor for main import processes
     * Small pool because each import consumes significant resources
     */
    @Bean("importExecutor")
    public TaskExecutor importExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Small pool for full imports
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(10);

        // Thread Configuration
        executor.setThreadNamePrefix("import-main-");
        executor.setKeepAliveSeconds(60);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(120);

        //Rejection policy: the caller executes the task if there is no space
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }

    /**
     * Executor for batch processing of room rates
     * Larger pool to parallelize data processing
     */
    @Bean("batchProcessor")
    public TaskExecutor batchProcessor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Largest pool for parallel batch processing
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);

        executor.setThreadNamePrefix("batch-proc-");
        executor.setKeepAliveSeconds(60);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        // More aggressive policy: discard tasks if there is no capacity
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());

        executor.initialize();
        return executor;
    }

    /**
     * Executor for I/O operations (file readings, database queries)
     * Dedicated pool for potentially blocking operations
     */
    @Bean("ioExecutor")
    public TaskExecutor ioExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Larger pool for I/O because tasks can block
        executor.setCorePoolSize(6);
        executor.setMaxPoolSize(12);
        executor.setQueueCapacity(200);

        executor.setThreadNamePrefix("io-");
        executor.setKeepAliveSeconds(120);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }

    /**
     * Executor for validations that can run in parallel
     * Pool optimized for CPU-intensive tasks
     */
    @Bean("validationExecutor")
    public TaskExecutor validationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Basado en número de cores disponibles
        int cores = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(cores);
        executor.setMaxPoolSize(cores * 2);
        executor.setQueueCapacity(500);

        executor.setThreadNamePrefix("validation-");
        executor.setKeepAliveSeconds(30);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(15);

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}