package com.kynsof.share.core.infrastructure.services;

import com.kynsof.share.core.domain.service.IReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Configuration
public class ReportGeneratorService implements IReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ReportGeneratorService.class);
    private final RestTemplate restTemplate;
    @Value("${report.service.url:http://localhost:8097/api/report}")
    private  String reportServiceUrl;

    public ReportGeneratorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public byte[] generateReport(Map<String, Object> parameters, String jasperReportCode) {
        try {
            logger.info("Calling report service at URL: {}", reportServiceUrl);
            logger.info("Parameters: {}", parameters);
            logger.info("Jasper Report Code: {}", jasperReportCode);

            HttpEntity<Object> request = createGenerateTemplateRequest(parameters, jasperReportCode);

            ResponseEntity<byte[]> response = restTemplate.postForEntity(
                    reportServiceUrl,
                    request,
                    byte[].class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Report generated successfully. Response size: {} bytes",
                        response.getBody() != null ? response.getBody().length : 0);
                return response.getBody();
            }

            logger.error("Report service returned error status: {}", response.getStatusCode());
            throw new RuntimeException("Failed to generate report. Status: " + response.getStatusCode());

        } catch (RestClientException e) {
            logger.error("Error connecting to report service at URL: {}. Error: {}",
                    reportServiceUrl, e.getMessage(), e);
            throw new RuntimeException("Failed to connect to report service: " + e.getMessage(), e);
        }
    }



    private HttpEntity<Object> createGenerateTemplateRequest(Map<String, Object> parameters, String jasperReportCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear el body exactamente como lo tienes en Postman
        Map<String, Object> requestBody = Map.of(
                "parameters", parameters,
                "reportFormatType", "PDF",
                "jasperReportCode", jasperReportCode,
                "requestId", String.valueOf(System.currentTimeMillis()),
                "metadata", Map.of(
                        "timestamp", java.time.Instant.now().toString(),
                        "userAgent", "Payment-Service"
                )
        );

        logger.info("Request body for report service: {}", requestBody);

        return new HttpEntity<>(requestBody, headers);
    }
}
