package com.kynsof.share.core.infrastructure.services;

import com.kynsof.share.core.domain.service.IReportGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportGeneratorService implements IReportGenerator {

    private final RestTemplate restTemplate;
    private final String reportServiceUrl;

    public ReportGeneratorService(RestTemplate restTemplate, @Value("${report.service.url}")String reportServiceUrl) {
        this.restTemplate = restTemplate;
        this.reportServiceUrl = reportServiceUrl;
    }

    @Override
    public byte[] generateReport(Map<String, Object> parameters, String jasperReportCode) {
        ResponseEntity<byte[]> response = restTemplate.postForEntity(
                reportServiceUrl,
                createGenerateTemplateRequest(parameters, jasperReportCode),
                byte[].class
        );

        if (response.getStatusCode().is2xxSuccessful()) {

            return response.getBody();
        }
        throw new RuntimeException("Failed to generate report");
    }



    public GenerateTemplateRequest createGenerateTemplateRequest(  Map<String, Object> parameters, String jasperReportCode) {

        return new GenerateTemplateRequest(parameters, jasperReportCode); // jasperReportCode es el código del reporte
    }
}
