package com.kynsoft.finamer.invoicing.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Configuración de infraestructura para el procesamiento de importaciones
 * Optimizada para manejar alto volumen de room rates con paralelización controlada
 */
@Configuration
@EnableAsync
@EnableTransactionManagement
public class ImportProcessingConfig {

    /**
     * Executor para procesos principales de importación
     * Pool pequeño porque cada importación consume recursos significativos
     */
    @Bean("importExecutor")
    public TaskExecutor importExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Pool pequeño para importaciones completas
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(10);

        // Configuración de threads
        executor.setThreadNamePrefix("import-main-");
        executor.setKeepAliveSeconds(60);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(120);

        // Política de rechazo: el caller ejecuta la tarea si no hay espacio
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }

    /**
     * Executor para procesamiento de lotes (batches) de room rates
     * Pool más grande para paralelizar el procesamiento de datos
     */
    @Bean("batchProcessor")
    public TaskExecutor batchProcessor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Pool más grande para procesamiento paralelo de lotes
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);

        executor.setThreadNamePrefix("batch-proc-");
        executor.setKeepAliveSeconds(60);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        // Política más agresiva: descartar tareas si no hay capacidad
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());

        executor.initialize();
        return executor;
    }

    /**
     * Executor para operaciones de I/O (lectura de archivos, consultas a BD)
     * Pool dedicado para operaciones que pueden bloquear
     */
    @Bean("ioExecutor")
    public TaskExecutor ioExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Pool más grande para I/O porque las tareas pueden bloquear
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
     * Executor para validaciones que pueden ejecutarse en paralelo
     * Pool optimizado para CPU-intensive tasks
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