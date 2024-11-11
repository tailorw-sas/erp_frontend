package com.kynsoft.report.controller;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.Base64;

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

    @PostMapping(value = "/generate/template", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generateReport(@RequestBody GenerateTemplateRequest request) {
        Connection connection = null;
        try {
            JasperReportTemplateDto reportTemplateDto = reportService.findByTemplateCode(request.getJasperReportCode());
            logger.info("Report code: {}", request.getJasperReportCode());
            logger.info("Report format type: {}", request.getReportFormatType());
            logger.info("Parameters: {}", request.getParameters());
            logger.info("URL dataBase: {}", reportTemplateDto.getDbConectionDto().getUrl());

            // Cargar el archivo JRXML
            JasperReport jasperReport = loadJasperReportFromUrl(reportTemplateDto.getFile());

            // Crear la conexión de base de datos usando los datos del request
            connection = DriverManager.getConnection(reportTemplateDto.getDbConectionDto().getUrl(),
                    reportTemplateDto.getDbConectionDto().getUsername(), reportTemplateDto.getDbConectionDto().getPassword());

            // Llenar el reporte usando la conexión de base de datos proporcionada
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, request.getParameters(), connection);

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
