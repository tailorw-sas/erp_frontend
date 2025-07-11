package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.ReportS3UploadResult;
import com.kynsoft.report.domain.services.IReportS3Service;
import com.kynsoft.report.infrastructure.utils.ReportMinIOClientExtension;
import com.kynsoft.report.infrastructure.utils.ReportS3ConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class ReportS3Service implements IReportS3Service {

    private final ReportS3ConfigurationProperties config;
    private final ReportMinIOClientExtension reportMinioClient;

    public ReportS3Service(ReportS3ConfigurationProperties config,
                           ReportMinIOClientExtension reportMinioClient) {
        this.config = config;
        this.reportMinioClient = reportMinioClient;
    }

    @Override
    public ReportS3UploadResult uploadReportToS3(String serverRequestId, byte[] reportData, String fileName, String contentType) {

        String s3ObjectKey = generateS3ObjectKey(serverRequestId, fileName);

        for (int attempt = 1; attempt <= config.getRetryAttempts(); attempt++) {
            try {
                log.info("Uploading report to MinIO | ServerRequestId: {} | Attempt: {} | ObjectKey: {}",
                        serverRequestId, attempt, s3ObjectKey);

                // Upload usando el nuevo método
                reportMinioClient.saveWithCustomKey(s3ObjectKey, reportData, contentType, config.getBucketName());

                // Generar presigned URL
                String preSignedUrl = reportMinioClient.generatePresignedDownloadUrl(
                        config.getBucketName(),
                        s3ObjectKey,
                        config.getPresignedUrlExpiryHours()
                );

                log.info("Report uploaded successfully to MinIO | ServerRequestId: {} | ObjectKey: {}",
                        serverRequestId, s3ObjectKey);

                return ReportS3UploadResult.builder()
                        .success(true)
                        .s3ObjectKey(s3ObjectKey)
                        .preSignedUrl(preSignedUrl)
                        .expirationDate(LocalDateTime.now().plusDays(config.getRetentionDays()))
                        .attemptNumber(attempt)
                        .build();

            } catch (Exception e) {
                log.warn("MinIO upload attempt {} failed for ServerRequestId: {} | Error: {}",
                        attempt, serverRequestId, e.getMessage());

                if (attempt == config.getRetryAttempts()) {
                    log.error("All MinIO upload attempts failed for ServerRequestId: {}",
                            serverRequestId, e);

                    return ReportS3UploadResult.builder()
                            .success(false)
                            .errorMessage(e.getMessage())
                            .attemptNumber(attempt)
                            .build();
                }

                // Exponential backoff: 2^attempt * baseDelay
                long delay = (long) (Math.pow(2, attempt) * config.getRetryBaseDelay());
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        return ReportS3UploadResult.builder()
                .success(false)
                .errorMessage("Max retry attempts exceeded")
                .attemptNumber(config.getRetryAttempts())
                .build();
    }

    @Override
    public String generateS3ObjectKey(String serverRequestId, String fileName) {
        LocalDate today = LocalDate.now();
        return String.format("reports/%s/%s/%s",
                today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                serverRequestId,
                fileName
        );
    }

    @Override
    public String generatePreSignedDownloadUrl(String s3ObjectKey, Duration expiration) {
        try {
            int hours = (int) expiration.toHours();
            return reportMinioClient.generatePresignedDownloadUrl(config.getBucketName(), s3ObjectKey, hours);
        } catch (Exception e) {
            log.error("Error generating presigned URL for: {}", s3ObjectKey, e);
            return null;
        }
    }

    @Override
    public boolean objectExists(String s3ObjectKey) {
        return reportMinioClient.objectExists(config.getBucketName(), s3ObjectKey);
    }

    @Override
    public boolean deleteObject(String s3ObjectKey) {
        return reportMinioClient.deleteObject(config.getBucketName(), s3ObjectKey);
    }

    @Override
    public boolean isS3Available() {
        return reportMinioClient.isReportsBucketAccessible();
    }
}
