package com.kynsoft.report.controller;

import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateCommand;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateMessage;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateRequest;
import com.kynsoft.report.applications.query.reportTemplate.GetReportParameterByCodeQuery;
import com.kynsoft.report.applications.query.reportTemplate.GetReportParameterByCodeResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final IMediator mediator;
    private final IJasperReportTemplateService reportService;
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    public ReportController(IMediator mediator, IJasperReportTemplateService reportService) {
        this.mediator = mediator;
        this.reportService = reportService;
    }

    @PostMapping(value = "/generate-template", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> getTemplate(@RequestBody GenerateTemplateRequest request) {

        GenerateTemplateCommand command = new GenerateTemplateCommand(request.getParameters(),
                request.getJasperReportCode(), request.getReportFormatType());
        GenerateTemplateMessage response = mediator.send(command);

        // Return the PDF as a ResponseEntity with headers
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(response.getResult());
    }

    @GetMapping("/parameters/{reportCode}")
    public ResponseEntity<?> getReportParameters(@PathVariable String reportCode) {
        GetReportParameterByCodeQuery query = new GetReportParameterByCodeQuery(reportCode);
        GetReportParameterByCodeResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/parameters")
//    public ResponseEntity<List<String>> getParameters(@RequestParam("url") String url_jrxml) {
//        List<String> parameters = new ArrayList<>();
//
//        try (InputStream reportStream = new URL(url_jrxml).openStream()) {
//            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
//            JRParameter[] reportParameters = jasperReport.getParameters();
//            for (JRParameter param : reportParameters) {
//                if (!param.isSystemDefined()) {
//                    parameters.add(param.getName());
//                }
//            }
//        } catch (JRException | IOException e) {
//            logger.error("Error getting parameters",  e);
//        }
//        return ResponseEntity.ok(parameters);
//    }

    @PostMapping(value = "/execute-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> executeReport(@RequestBody GenerateTemplateRequest request) {
        Connection connection = null;
        try {
            JasperReportTemplateDto reportTemplateDto = reportService.findByTemplateCode(request.getJasperReportCode());
            logger.info("Report code: {}", request.getJasperReportCode());
            logger.info("Report format type: {}", request.getReportFormatType());
            logger.info("Parameters: {}", request.getParameters());

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
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
                configuration.setDetectCellType(true);
                configuration.setCollapseRowSpan(false);
                exporter.setConfiguration(configuration);
                exporter.exportReport();
            } else if ("CSV".equalsIgnoreCase(request.getReportFormatType())) {
                net.sf.jasperreports.engine.export.JRCsvExporter exporter = new net.sf.jasperreports.engine.export.JRCsvExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new net.sf.jasperreports.export.SimpleWriterExporterOutput(outputStream));
                exporter.exportReport();
            } else {
                JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            }

            // Convertir el reporte a Base64
            String base64Report = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{\"base64Report\": \"" + base64Report + "\"}");
        } catch (Exception e) {
            logger.error("Report code: {}", request.getJasperReportCode(), e);
            throw new RuntimeException("Error generating report", e);
        } finally {
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

