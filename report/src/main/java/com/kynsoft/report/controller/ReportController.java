package com.kynsoft.report.controller;

import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateCommand;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateMessage;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateRequest;
import com.kynsoft.report.applications.query.reportTemplate.GetReportParameterByCodeQuery;
import com.kynsoft.report.applications.query.reportTemplate.GetReportParameterByCodeResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import com.kynsoft.report.infrastructure.enums.ReportFormatType;
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
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kynsoft.report.infrastructure.enums.ReportFormatType.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final IMediator mediator;
    private final IJasperReportTemplateService reportService;
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final ConcurrentHashMap<String, JasperReport> jasperCache = new ConcurrentHashMap<>();
    private final AtomicInteger compileCounter = new AtomicInteger(0);

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

    @PostMapping(value = "/execute-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> executeReport(@RequestBody GenerateTemplateRequest request) {
        Connection connection = null;
        try {
            JasperReportTemplateDto reportTemplateDto = reportService.findByTemplateCode(request.getJasperReportCode());
            logger.info("Report code: {}", request.getJasperReportCode());
            logger.info("Report format type: {}", request.getReportFormatType());
            logger.info("Parameters: {}", request.getParameters());

            // Use extracted parameter conversion method
            Map<String, Object> convertedParams = convertParameters(request.getParameters());
            logger.info("URL dataBase: {}", reportTemplateDto.getDbConectionDto().getUrl());

            // Cargar el archivo JRXML
            JasperReport jasperReport = loadJasperReportFromUrl(reportTemplateDto.getFile());

            // Crear la conexión de base de datos usando los datos del request
            connection = DriverManager.getConnection(reportTemplateDto.getDbConectionDto().getUrl(),
                    reportTemplateDto.getDbConectionDto().getUsername(), reportTemplateDto.getDbConectionDto().getPassword());

            // Llenar el reporte usando la conexión de base de datos proporcionada
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, convertedParams, connection);

            // Use try-with-resources for ByteArrayOutputStream
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                String contentType;
                String fileName;
                ReportFormatType formatType = ReportFormatType.fromString(request.getReportFormatType());
                switch (formatType) {
                    case XLS:
                        JRXlsxExporter exporterxls = new JRXlsxExporter();
                        exporterxls.setExporterInput(new SimpleExporterInput(jasperPrint));
                        exporterxls.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
                        configuration.setDetectCellType(true);
                        configuration.setCollapseRowSpan(false);
                        exporterxls.setConfiguration(configuration);
                        exporterxls.exportReport();
                        break;
                    case CSV:
                        net.sf.jasperreports.engine.export.JRCsvExporter exporterCsv = new net.sf.jasperreports.engine.export.JRCsvExporter();
                        exporterCsv.setExporterInput(new SimpleExporterInput(jasperPrint));
                        exporterCsv.setExporterOutput(new net.sf.jasperreports.export.SimpleWriterExporterOutput(outputStream));
                        exporterCsv.exportReport();
                        break;
                    case PDF:
                    default:
                        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
                        break;
                }

            String base64Report = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{\"base64Report\": \"" + base64Report + "\"}");
            }
        } catch (Exception e) {
            logger.error("Report code: {}", request.getJasperReportCode(), e);
            return ResponseEntity.status(500).body("{\"error\": \"Error generating report\", \"details\": \"" + e.getMessage().replace("\"", "\\\"") + "\"}");
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

    /**
     * Convierte los parámetros recibidos, procesando solo aquellos de categoría "REPORT".
     * Solo los parámetros cuya categoría sea "REPORT" serán incluidos en el resultado.
     * Si el parámetro es un String "true"/"false" se convierte a Boolean.
     * Si el nombre del parámetro contiene "date" y es un String, intenta convertirlo a java.sql.Date.
     * Si el valor es una lista, la convierte en String separado por comas.
     *
     * Nota: Para que la categoría esté disponible aquí, se debe incluir en la estructura de originalParams
     *       como un campo adicional (por ejemplo, usando un DTO con getCategory()).
     */
    private Map<String, Object> convertParameters(Map<String, Object> originalParams) {
        Map<String, Object> convertedParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : originalParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String && ("true".equalsIgnoreCase((String) value) || "false".equalsIgnoreCase((String) value))) {
                convertedParams.put(key, Boolean.valueOf((String) value));
            } else if (value instanceof Boolean) {
                convertedParams.put(key, value);
            } else if (key.toLowerCase().contains("date") && value instanceof String) {
                try {
                    LocalDate localDate = LocalDate.parse((String) value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    convertedParams.put(key, java.sql.Date.valueOf(localDate));
                } catch (Exception ex) {
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

            logger.debug("Parametro convertido: {} = {}", key, convertedParams.get(key));
        }
        return convertedParams;
    }

    private JasperReport loadJasperReportFromUrl(String templateUrl) {
        try {
            return jasperCache.computeIfAbsent(templateUrl, url -> {
                try {
                    logger.info("Compiling JRXML template from URL: {}", url);
                    compileCounter.incrementAndGet();
                    try (InputStream in = new URL(url).openStream();
                         ByteArrayInputStream inputStream = new ByteArrayInputStream(in.readAllBytes())) {
                        return JasperCompileManager.compileReport(inputStream);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error compiling JRXML template from URL: " + url, e);
                }
            });
        } catch (RuntimeException e) {
            throw e;
        }
    }
}

