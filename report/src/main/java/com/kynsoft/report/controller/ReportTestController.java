package com.kynsoft.report.controller;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;

import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/reports-test")
public class ReportTestController {

    private final IMediator mediator;
    private final IJasperReportTemplateService reportService;
    private static final Logger logger = LoggerFactory.getLogger(ReportTestController.class);
    private final JdbcTemplate jdbcTemplate;

    public ReportTestController(IMediator mediator
                            , IJasperReportTemplateService reportService, JdbcTemplate jdbcTemplate
    ) {
        this.mediator = mediator;
        this.reportService = reportService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/parametros")
    public ResponseEntity<List<String>> obtenerParametros(@RequestParam("url") String urlJRXML) {
        List<String> parametros = new ArrayList<>();

        try (InputStream reporteStream = new URL(urlJRXML).openStream()) {
            // 1. Compilar el archivo JRXML desde la URL
            JasperReport jasperReport = JasperCompileManager.compileReport(reporteStream);

            // 2. Obtener parámetros del reporte
            JRParameter[] reportParameters = jasperReport.getParameters();

            // 3. Filtrar parámetros internos de JasperReports
            for (JRParameter param : reportParameters) {
                if (!param.isSystemDefined()) {
                    parametros.add(param.getName());
                }
            }

        } catch (JRException | IOException e) {
            // Manejar la excepción (logs, rethrow, etc.)
            e.printStackTrace();
        }
        return ResponseEntity.ok(parametros);
    }

    @PostMapping(value = "/generate/template", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generateReport(@RequestBody GenerateTemplateRequest request) {
        Connection connection = null;
        try {
            JasperReportTemplateDto reportTemplateDto = reportService.findByTemplateCode(request.getJasperReportCode());
            logger.info("Report code: {}", request.getJasperReportCode());
            logger.info("Report format type: {}", request.getReportFormatType());
            logger.info("Parameters: {}", request.getParameters());
            // Convertir Strings a java.sql.Date si corresponde
            Map<String, Object> originalParams = request.getParameters();
            Map<String, Object> convertedParams = new HashMap<>();

            for (Map.Entry<String, Object> entry : originalParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (key.toLowerCase().contains("date") && value instanceof String) {
                    try {
                        java.sql.Date dateValue = java.sql.Date.valueOf((String) value);
                        convertedParams.put(key, dateValue);
                    } catch (IllegalArgumentException ex) {
                        logger.warn("El parámetro {} con valor '{}' no pudo convertirse a java.sql.Date", key, value);
                        convertedParams.put(key, value);
                    }
                } else if (value instanceof List<?>) {
                    List<?> list = (List<?>) value;
                    String joined = String.join(",", list.stream().map(Object::toString).toArray(String[]::new));
                    convertedParams.put(key, joined);
                } else {
                    convertedParams.put(key, value);
                }
            }
            logger.info("URL dataBase: {}", reportTemplateDto.getDbConectionDto().getUrl());

            // Cargar el archivo JRXML
            JasperReport jasperReport = loadJasperReportFromUrl(reportTemplateDto.getFile());

            // Crear la conexión de base de datos usando los datos del request
            connection = DriverManager.getConnection(reportTemplateDto.getDbConectionDto().getUrl(),
                    reportTemplateDto.getDbConectionDto().getUsername(), reportTemplateDto.getDbConectionDto().getPassword());

            // Llenar el reporte usando la conexión de base de datos proporcionada
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, convertedParams, connection);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            String contentType;
            String fileName;

            if ("XLS".equalsIgnoreCase(request.getReportFormatType())) {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                fileName = "report.xlsx";

                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
                configuration.setDetectCellType(true);
                configuration.setCollapseRowSpan(false);
                exporter.setConfiguration(configuration);
                exporter.exportReport();
            } else {
                contentType = MediaType.APPLICATION_PDF_VALUE;
                fileName = "report.pdf";
                JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            }

            // Convertir el reporte a Base64
            String base64Report = Base64.getEncoder().encodeToString(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"base64Report\": \"" + base64Report + "\"}");

        } catch (Exception e) {
            logger.error("Report code: {}", request.getJasperReportCode(), e);
            throw new RuntimeException("Error generating report", e);
        } finally {
            // Cerrar la conexión si está abierta
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    logger.error("Error closing database connection", e);
                }
            }
        }
    }

    private JasperReport loadJasperReportFromUrl(String templateUrl) throws JRException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new URL(templateUrl).openStream().readAllBytes())) {
            return JasperCompileManager.compileReport(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Error loading JRXML template from URL: " + templateUrl, e);
        }
    }
}
