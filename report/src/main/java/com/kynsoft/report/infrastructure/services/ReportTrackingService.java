package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.domain.dto.ReportGenerationResponse;
import com.kynsoft.report.domain.dto.ReportProcessingDto;
import com.kynsoft.report.domain.enums.ReportStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReportTrackingService {

    private static final Logger logger = LoggerFactory.getLogger(ReportTrackingService.class);
    private static final String REPORT_KEY_PREFIX = "report:tracking:";
    private static final Duration REPORT_TTL = Duration.ofHours(24);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void createReportTracking(String serverRequestId, String clientRequestId, String jasperReportCode, String reportFormatType,
                                     String originalRequestJson) {
        ReportProcessingDto tracking = ReportProcessingDto.builder()
                .serverRequestId(serverRequestId)
                .clientRequestId(clientRequestId)
                .jasperReportCode(jasperReportCode)
                .reportFormatType(reportFormatType)
                .status(ReportStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .originalRequestJson(originalRequestJson)
                .build();

        saveTracking(tracking);

        // Log para auditoría
        logger.info("Report tracking created | ServerID: {} | ClientID: {} | ReportCode: {}",
                serverRequestId, clientRequestId, jasperReportCode);
    }

    public void updateReportStatus(String serverRequestId, ReportStatus status, String errorMessage) {
        Optional<ReportProcessingDto> trackingOpt = getReportTracking(serverRequestId);
        if (trackingOpt.isPresent()) {
            ReportProcessingDto tracking = trackingOpt.get();
            tracking.setStatus(status);
            tracking.setErrorMessage(errorMessage);
            tracking.setUpdatedAt(LocalDateTime.now());
            saveTracking(tracking);

            logger.info("Report status updated | ServerID: {} | ClientID: {} | Status: {}",
                    serverRequestId, tracking.getClientRequestId(), status);
        }
    }

    public void completeReportProcessing(String serverRequestId, String reportBase64, Long fileSizeBytes) {
        Optional<ReportProcessingDto> trackingOpt = getReportTracking(serverRequestId);
        if (trackingOpt.isPresent()) {
            ReportProcessingDto tracking = trackingOpt.get();
            tracking.setStatus(ReportStatus.COMPLETED);
            tracking.setReportBase64(reportBase64);
            tracking.setFileSizeBytes(fileSizeBytes);
            tracking.setUpdatedAt(LocalDateTime.now());
            saveTracking(tracking);

            logger.info("Report completed | ServerID: {} | ClientID: {} | Size: {} bytes",
                    serverRequestId, tracking.getClientRequestId(), fileSizeBytes);
        }
    }

    public void completeReportProcessingWithS3(String serverRequestId, ReportGenerationResponse reportResponse) {
        Optional<ReportProcessingDto> trackingOpt = getReportTracking(serverRequestId);

        if (trackingOpt.isPresent()) {
            ReportProcessingDto tracking = trackingOpt.get();

            // Actualizar con datos híbridos
            tracking.setStatus(ReportStatus.COMPLETED);
            tracking.setUpdatedAt(LocalDateTime.now());
            tracking.setUseS3Storage(reportResponse.isUseS3Storage());
            tracking.setStorageMethod(reportResponse.getStorageMethod());
            tracking.setFileSizeBytes(reportResponse.getFileSizeBytes());

            if (reportResponse.isUseS3Storage()) {
                // Datos S3
                tracking.setS3PreSignedUrl(reportResponse.getS3DownloadUrl());
                tracking.setS3ExpirationDate(reportResponse.getExpirationDate());
                tracking.setReportBase64(null); // Limpiar base64 para ahorrar memoria
            } else {
                // Fallback base64
                tracking.setReportBase64(reportResponse.getBase64Report());
            }

            // Guardar en Redis
            saveTracking(tracking);

            logger.info("Report processing completed | ServerID: {} | Storage: {} | Size: {} bytes",
                    serverRequestId, tracking.getStorageMethod(), tracking.getFileSizeBytes());
        }
    }

    public Optional<ReportProcessingDto> getReportTracking(String serverRequestId) {
        try {
            String json = redisTemplate.opsForValue().get(REPORT_KEY_PREFIX + serverRequestId);
            if (json != null) {
                return Optional.of(objectMapper.readValue(json, ReportProcessingDto.class));
            }
        } catch (Exception e) {
            logger.error("Error retrieving report tracking for serverRequestId: {}", serverRequestId, e);
        }
        return Optional.empty();
    }

    public void saveTracking(ReportProcessingDto tracking) {
        try {
            String json = objectMapper.writeValueAsString(tracking);
            redisTemplate.opsForValue().set(
                    REPORT_KEY_PREFIX + tracking.getServerRequestId(),
                    json,
                    REPORT_TTL
            );
        } catch (Exception e) {
            logger.error("Error saving report tracking for serverRequestId: {}",
                    tracking.getServerRequestId(), e);
        }
    }

    public void deleteReportTracking(String serverRequestId) {
        redisTemplate.delete(REPORT_KEY_PREFIX + serverRequestId);
        logger.info("Report tracking deleted for serverRequestId: {}", serverRequestId);
    }
}
