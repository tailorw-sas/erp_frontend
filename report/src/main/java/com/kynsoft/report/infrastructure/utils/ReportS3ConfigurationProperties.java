package com.kynsoft.report.infrastructure.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "reports.s3")
@Data
@Component
public class ReportS3ConfigurationProperties {
    private String bucketName;
    private int retentionDays = 3;
    private int retryAttempts = 3;
    private long retryBaseDelay = 2000L;
    private boolean cleanupEnabled = true;
    private String cleanupCron = "0 0 2 * * ?";
    private boolean fallbackToBase64 = true;
    private int presignedUrlExpiryHours = 72; // 3 días para MinIO
}
