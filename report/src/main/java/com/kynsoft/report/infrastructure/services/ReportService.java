package com.kynsoft.report.infrastructure.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsoft.report.domain.services.IReportService;
import net.sf.jasperreports.engine.*;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ReportService implements IReportService {
    private final RestTemplate restTemplate;

    public ReportService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public byte[] generatePdfReport(Map<String, Object> parameters, String jrxmlUrl, JREmptyDataSource jrEmptyDataSource) {
        InputStream inputStream = new ByteArrayInputStream(Objects.requireNonNull(restTemplate.getForObject(jrxmlUrl, byte[].class)));
        JasperReport jasperReport = null;
        try {
            jasperReport = JasperCompileManager.compileReport(inputStream);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }

        // Imprimir parámetros para debugging
        parameters.forEach((key, value) -> System.out.println(key + ": " + value));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrEmptyDataSource);
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public String getReportParameters(String jrxmlUrl) {
        // Obtener el archivo JRXML como un arreglo de bytes desde la URL
        byte[] data = restTemplate.getForObject(jrxmlUrl, byte[].class);
        InputStream inputStream = new ByteArrayInputStream(data);
        JasperReport jasperReport = null;
        try {
            jasperReport = JasperCompileManager.compileReport(inputStream);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }

        // Preparar el mapa para almacenar los detalles de los parámetros
        Map<String, Map<String, String>> paramMap = new HashMap<>();
        for (JRParameter param : jasperReport.getParameters()) {
            if (!param.isSystemDefined() && param.isForPrompting()) { // Solo parámetros definidos por el usuario y que son promptables
                Map<String, String> details = new HashMap<>();
                details.put("description", param.getDescription() != null ? param.getDescription() : "No description");
                details.put("type", param.getValueClassName());  // Añadir tipo de dato
                paramMap.put(param.getName(), details);
            }
        }

        // Convertir el mapa a una cadena JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(paramMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
