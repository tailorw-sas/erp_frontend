package com.kynsoft.report.infrastructure.messaging;

import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateRequest;
import com.kynsoft.report.controller.ReportController;
import com.kynsoft.report.domain.events.ReportProcessingEvent;
import com.kynsoft.report.infrastructure.services.ReportTrackingService;
import com.kynsoft.report.domain.enums.ReportStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReportEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ReportEventConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReportController reportController;

    @Autowired
    private ReportTrackingService reportTrackingService;

    // ✅ FIXED: Recibir ConsumerRecord directamente y extraer el value
    @KafkaListener(topics = "report.processing.events", groupId = "report-processing-group")
    public void handleReportProcessingEvent(ConsumerRecord<String, Object> consumerRecord) {

        ReportProcessingEvent event = null;
        String messageKey = consumerRecord.key();
        Object eventPayload = consumerRecord.value();

        try {
            logger.info("Received consumer record | Topic: {} | Partition: {} | Offset: {} | Key: {}",
                    consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), messageKey);

            // ✅ FIXED: Convertir el payload (value del record) a ReportProcessingEvent
            if (eventPayload instanceof ReportProcessingEvent) {
                // Ya es del tipo correcto
                event = (ReportProcessingEvent) eventPayload;
            } else {
                // Convertir usando ObjectMapper (Map -> ReportProcessingEvent)
                event = objectMapper.convertValue(eventPayload, ReportProcessingEvent.class);
            }

            logger.info("Processing report event | ServerID: {} | ClientID: {} | EventType: {} | MessageKey: {}",
                    event.getServerRequestId(), event.getClientRequestId(), event.getEventType(), messageKey);

            if ("START_PROCESSING".equals(event.getEventType())) {
                processReport(event);
            } else {
                logger.warn("Unknown event type: {} | ServerID: {}", event.getEventType(), event.getServerRequestId());
            }

        } catch (Exception e) {
            logger.error("Error processing report event | ServerID: {} | ClientID: {} | MessageKey: {} | Error: {}",
                    event != null ? event.getServerRequestId() : "unknown",
                    event != null ? event.getClientRequestId() : "unknown",
                    messageKey,
                    e.getMessage(), e);

            if (event != null) {
                reportTrackingService.updateReportStatus(
                        event.getServerRequestId(),
                        ReportStatus.FAILED,
                        "Error processing event: " + e.getMessage()
                );
            }
        }
    }

    private void processReport(ReportProcessingEvent event) {
        String serverRequestId = event.getServerRequestId();
        String clientRequestId = event.getClientRequestId();

        try {
            logger.info("Starting report processing | ServerID: {} | ClientID: {} | ReportCode: {}",
                    serverRequestId, clientRequestId, event.getJasperReportCode());

            // Actualizar estado a PROCESSING
            reportTrackingService.updateReportStatus(serverRequestId, ReportStatus.PROCESSING, null);

            // Crear request para el controlador
            GenerateTemplateRequest request = new GenerateTemplateRequest();
            request.setRequestId(serverRequestId);
            request.setJasperReportCode(event.getJasperReportCode());
            request.setReportFormatType(event.getReportFormatType());
            request.setParameters(event.getParameters());

            // Ejecutar el reporte
            logger.info("Executing report | ServerID: {} | ReportCode: {} | Format: {}",
                    serverRequestId, event.getJasperReportCode(), event.getReportFormatType());

            ResponseEntity<String> response = reportController.executeReportInternal(request);

            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                String base64Report = extractBase64FromResponse(responseBody);
                Long fileSize = (long) base64Report.length();

                reportTrackingService.completeReportProcessing(serverRequestId, base64Report, fileSize);

                logger.info("Report processing completed successfully | ServerID: {} | ClientID: {} | FileSize: {} bytes",
                        serverRequestId, clientRequestId, fileSize);
            } else {
                String errorMessage = "Report generation failed with status: " + response.getStatusCode();
                reportTrackingService.updateReportStatus(serverRequestId, ReportStatus.FAILED, errorMessage);
                logger.error("Report processing failed | ServerID: {} | ClientID: {} | Status: {} | Error: {}",
                        serverRequestId, clientRequestId, response.getStatusCode(), errorMessage);
            }

        } catch (Exception e) {
            String errorMessage = "Error processing report: " + e.getMessage();
            reportTrackingService.updateReportStatus(serverRequestId, ReportStatus.FAILED, errorMessage);
            logger.error("Report processing failed | ServerID: {} | ClientID: {} | Error: {}",
                    serverRequestId, clientRequestId, errorMessage, e);
        }
    }

    private String extractBase64FromResponse(String responseBody) {
        try {
            if (responseBody == null || responseBody.trim().isEmpty()) {
                throw new RuntimeException("Empty response body");
            }

            var jsonNode = objectMapper.readTree(responseBody);

            if (!jsonNode.has("base64Report")) {
                throw new RuntimeException("No 'base64Report' field in response");
            }

            String base64Report = jsonNode.get("base64Report").asText();

            if (base64Report == null || base64Report.trim().isEmpty()) {
                throw new RuntimeException("Empty base64Report value");
            }

            return base64Report;

        } catch (Exception e) {
            logger.error("Error extracting base64 from response | Response: {} | Error: {}", responseBody, e.getMessage());
            throw new RuntimeException("Error extracting base64 from response: " + e.getMessage(), e);
        }
    }
}
