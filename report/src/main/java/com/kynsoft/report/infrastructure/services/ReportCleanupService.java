package com.kynsoft.report.infrastructure.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsoft.report.domain.dto.CleanupJobDto;
import com.kynsoft.report.domain.dto.CleanupPreviewResult;
import com.kynsoft.report.domain.dto.CleanupResult;
import com.kynsoft.report.domain.services.IReportCleanupService;
import com.kynsoft.report.infrastructure.utils.ReportMinIOClientExtension;
import com.kynsoft.report.infrastructure.utils.ReportS3ConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ReportCleanupService implements IReportCleanupService {

    private final ReportS3ConfigurationProperties config;
    private final ReportMinIOClientExtension reportMinioClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CLEANUP_JOB_KEY_PREFIX = "cleanup:job:";
    private static final Duration CLEANUP_JOB_TTL = Duration.ofDays(7); // Mantener logs 7 días

    public ReportCleanupService(ReportS3ConfigurationProperties config,
                                ReportMinIOClientExtension reportMinioClient,
                                RedisTemplate<String, String> redisTemplate,
                                ObjectMapper objectMapper) {
        this.config = config;
        this.reportMinioClient = reportMinioClient;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    @Scheduled(cron = "${reports.s3.cleanup-cron}")
    @ConditionalOnProperty(name = "reports.s3.cleanup-enabled", havingValue = "true")
    public void executeScheduledCleanup() {
        if (!config.isCleanupEnabled()) {
            log.debug("Cleanup is disabled, skipping scheduled cleanup");
            return;
        }

        log.info("Starting scheduled cleanup process");

        // Calcular fecha de cleanup: hoy - retention days
        LocalDate cutoffDate = LocalDate.now().minusDays(config.getRetentionDays());

        try {
            CleanupResult result = cleanupReportsForDate(cutoffDate);

            if (result.isSuccess()) {
                log.info("Scheduled cleanup completed successfully | Date: {} | Files deleted: {} | Total size: {} bytes",
                        cutoffDate, result.getFilesDeletedSuccessfully(), result.getTotalSizeBytes());
            } else {
                log.error("Scheduled cleanup failed | Date: {} | Error: {}",
                        cutoffDate, result.getErrorMessage());
            }

        } catch (Exception e) {
            log.error("Unexpected error during scheduled cleanup | Date: {}", cutoffDate, e);
        }
    }

    @Override
    public CleanupResult cleanupReportsForDate(LocalDate date) {
        String jobId = UUID.randomUUID().toString();
        LocalDateTime startTime = LocalDateTime.now();

        log.info("Starting cleanup for date: {} | JobId: {}", date, jobId);

        // Crear job tracking
        CleanupJobDto job = CleanupJobDto.builder()
                .jobId(jobId)
                .cleanupDate(date)
                .startTime(startTime)
                .status("RUNNING")
                .build();

        saveCleanupJob(job);

        try {
            // Generar prefijo de la carpeta del día
            String datePrefix = String.format("reports/%s/",
                    date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            log.info("Cleanup target folder: {} | JobId: {}", datePrefix, jobId);

            // Listar todos los objetos de ese día
            List<String> objectsToDelete = listObjectsWithPrefix(datePrefix);

            if (objectsToDelete.isEmpty()) {
                log.info("No files found for cleanup | Date: {} | JobId: {}", date, jobId);

                CleanupResult result = CleanupResult.builder()
                        .cleanupDate(date)
                        .totalFilesFound(0)
                        .filesDeletedSuccessfully(0)
                        .filesFailed(0)
                        .failedFiles(new ArrayList<>())
                        .totalSizeBytes(0L)
                        .executionTime(Duration.between(startTime, LocalDateTime.now()))
                        .success(true)
                        .build();

                job.setResult(result);
                job.setStatus("COMPLETED");
                job.setEndTime(LocalDateTime.now());
                saveCleanupJob(job);

                return result;
            }

            // Ejecutar cleanup
            List<String> failedFiles = new ArrayList<>();
            int successCount = 0;
            long totalSize = 0L;

            for (String objectKey : objectsToDelete) {
                try {
                    // Obtener tamaño antes de eliminar (opcional)
                    // totalSize += getObjectSize(objectKey); // Implementar si necesitas tracking de tamaño

                    boolean deleted = reportMinioClient.deleteObject(config.getBucketName(), objectKey);

                    if (deleted) {
                        successCount++;
                        log.debug("Successfully deleted: {}", objectKey);
                    } else {
                        failedFiles.add(objectKey);
                        log.warn("Failed to delete: {}", objectKey);
                    }

                } catch (Exception e) {
                    failedFiles.add(objectKey);
                    log.error("Error deleting object: {} | Error: {}", objectKey, e.getMessage());
                }
            }

            LocalDateTime endTime = LocalDateTime.now();
            Duration executionTime = Duration.between(startTime, endTime);

            CleanupResult result = CleanupResult.builder()
                    .cleanupDate(date)
                    .totalFilesFound(objectsToDelete.size())
                    .filesDeletedSuccessfully(successCount)
                    .filesFailed(failedFiles.size())
                    .failedFiles(failedFiles)
                    .totalSizeBytes(totalSize)
                    .executionTime(executionTime)
                    .success(failedFiles.isEmpty())
                    .build();

            // Actualizar job
            job.setResult(result);
            job.setStatus(result.isSuccess() ? "COMPLETED" : "FAILED");
            job.setEndTime(endTime);
            saveCleanupJob(job);

            log.info("Cleanup completed | Date: {} | JobId: {} | Success: {} | Files deleted: {}/{} | Duration: {}ms",
                    date, jobId, result.isSuccess(), successCount, objectsToDelete.size(),
                    executionTime.toMillis());

            return result;

        } catch (Exception e) {
            log.error("Cleanup failed | Date: {} | JobId: {} | Error: {}", date, jobId, e.getMessage(), e);

            CleanupResult result = CleanupResult.builder()
                    .cleanupDate(date)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .executionTime(Duration.between(startTime, LocalDateTime.now()))
                    .build();

            job.setResult(result);
            job.setStatus("FAILED");
            job.setEndTime(LocalDateTime.now());
            saveCleanupJob(job);

            return result;
        }
    }

    @Override
    public CleanupResult cleanupReportsForDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Starting cleanup for date range: {} to {}", startDate, endDate);

        List<CleanupResult> results = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            CleanupResult result = cleanupReportsForDate(currentDate);
            results.add(result);
            currentDate = currentDate.plusDays(1);
        }

        // Consolidar resultados
        return consolidateResults(results, startDate, endDate);
    }

    @Override
    public CleanupPreviewResult previewCleanup(LocalDate date) {
        try {
            String datePrefix = String.format("reports/%s/",
                    date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            List<String> objectsToDelete = listObjectsWithPrefix(datePrefix);

            return CleanupPreviewResult.builder()
                    .targetDate(date)
                    .totalFilesFound(objectsToDelete.size())
                    .filePaths(objectsToDelete)
                    .build();

        } catch (Exception e) {
            log.error("Error previewing cleanup for date: {}", date, e);
            throw new RuntimeException("Error previewing cleanup", e);
        }
    }

    /**
     * Listar objetos con prefijo específico
     */
    private List<String> listObjectsWithPrefix(String prefix) {
        try {
            // ✅ Necesitarás implementar este método en ReportMinIOClientExtension
            return reportMinioClient.listObjectsWithPrefix(config.getBucketName(), prefix);
        } catch (Exception e) {
            log.error("Error listing objects with prefix: {}", prefix, e);
            throw new RuntimeException("Error listing objects", e);
        }
    }

    /**
     * Consolidar múltiples resultados de cleanup
     */
    private CleanupResult consolidateResults(List<CleanupResult> results, LocalDate startDate, LocalDate endDate) {
        int totalFiles = results.stream().mapToInt(CleanupResult::getTotalFilesFound).sum();
        int successFiles = results.stream().mapToInt(CleanupResult::getFilesDeletedSuccessfully).sum();
        int failedFiles = results.stream().mapToInt(CleanupResult::getFilesFailed).sum();

        List<String> allFailedFiles = results.stream()
                .flatMap(r -> r.getFailedFiles().stream())
                .toList();

        Duration totalDuration = results.stream()
                .map(CleanupResult::getExecutionTime)
                .reduce(Duration.ZERO, Duration::plus);

        return CleanupResult.builder()
                .cleanupDate(startDate) // Usar fecha de inicio para rango
                .totalFilesFound(totalFiles)
                .filesDeletedSuccessfully(successFiles)
                .filesFailed(failedFiles)
                .failedFiles(allFailedFiles)
                .executionTime(totalDuration)
                .success(failedFiles == 0)
                .build();
    }

    /**
     * Guardar job de cleanup en Redis
     */
    private void saveCleanupJob(CleanupJobDto job) {
        try {
            String json = objectMapper.writeValueAsString(job);
            redisTemplate.opsForValue().set(
                    CLEANUP_JOB_KEY_PREFIX + job.getJobId(),
                    json,
                    CLEANUP_JOB_TTL
            );
        } catch (Exception e) {
            log.error("Error saving cleanup job: {}", job.getJobId(), e);
        }
    }
}