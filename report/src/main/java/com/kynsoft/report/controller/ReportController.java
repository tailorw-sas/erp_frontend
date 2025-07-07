package com.kynsoft.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateCommand;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateMessage;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateRequest;
import com.kynsoft.report.applications.query.reportTemplate.GetReportParameterByCodeQuery;
import com.kynsoft.report.applications.query.reportTemplate.GetReportParameterByCodeResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.domain.dto.*;
import com.kynsoft.report.domain.enums.ReportStatus;
import com.kynsoft.report.domain.events.ReportProcessingEvent;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import com.kynsoft.report.infrastructure.enums.ReportFormatType;
import com.kynsoft.report.infrastructure.messaging.ReportEventProducer;
import com.kynsoft.report.infrastructure.services.ReportTrackingService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final IMediator mediator;
    private final IJasperReportTemplateService reportService;
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final ConcurrentHashMap<String, JasperReport> jasperCache = new ConcurrentHashMap<>();
    private final AtomicInteger compileCounter = new AtomicInteger(0);

    @Autowired
    private ReportEventProducer reportEventProducer;

    @Autowired
    private ReportTrackingService reportTrackingService;

    @Autowired
    private ObjectMapper objectMapper;

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

    @PostMapping(value = "/generate-async", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ReportSubmissionResponse>> generateReportAsync(
            @RequestBody GenerateTemplateRequest request) {
        try {
            // Generar el ID del servidor (este es el que usará el cliente para consultar)
            String serverRequestId = UUID.randomUUID().toString();

            // Preservar el ID del cliente para logs y auditoría
            String clientRequestId = request.getRequestId();

            // Serializar el request completo para auditoría
            String originalRequestJson = objectMapper.writeValueAsString(request);

            // Crear tracking inicial en Redis con ambos IDs
            reportTrackingService.createReportTracking(
                    serverRequestId,
                    clientRequestId,
                    request.getJasperReportCode(),
                    request.getReportFormatType(),
                    originalRequestJson
            );

            // Publicar evento para procesamiento
            ReportProcessingEvent event = ReportProcessingEvent.builder()
                    .serverRequestId(serverRequestId)
                    .clientRequestId(clientRequestId)
                    .jasperReportCode(request.getJasperReportCode())
                    .reportFormatType(request.getReportFormatType())
                    .parameters(request.getParameters())
                    .eventType("START_PROCESSING")
                    .timestamp(LocalDateTime.now().toString())
                    .originalRequestJson(originalRequestJson)
                    .build();

            reportEventProducer.publishReportProcessingEvent(event);

            // Respuesta con el SERVER REQUEST ID (no el del cliente)
            ReportSubmissionResponse response = ReportSubmissionResponse.builder()
                    .requestId(serverRequestId)  // ID del servidor
                    .message("Report submitted for processing")
                    .status("ACCEPTED")
                    .timestamp(System.currentTimeMillis())
                    .clientRequestId(clientRequestId)  // Opcional: devolver también el del cliente
                    .build();

            logger.info("Report submitted | ServerID: {} | ClientID: {} | ReportCode: {}",
                    serverRequestId, clientRequestId, request.getJasperReportCode());

            return ResponseEntity.accepted().body(ApiResponse.success(response));

        } catch (Exception e) {
            logger.error("Error submitting report | ClientID: {} | Error: {}",
                    request.getRequestId(), e.getMessage(), e);
            ErrorResponse error = new ErrorResponse("Error submitting report for processing", e.getMessage());
            return ResponseEntity.status(500).body(ApiResponse.error(error));
        }
    }

    @GetMapping("/status/{serverRequestId}")
    public ResponseEntity<ApiResponse<ReportStatusResponse>> getReportStatus(@PathVariable String serverRequestId) {
        Optional<ReportProcessingDto> tracking = reportTrackingService.getReportTracking(serverRequestId);

        if (tracking.isPresent()) {
            ReportProcessingDto dto = tracking.get();
            ReportStatusResponse response = ReportStatusResponse.builder()
                    .requestId(dto.getServerRequestId())  // ID del servidor
                    .clientRequestId(dto.getClientRequestId())  // ID del cliente para referencia
                    .jasperReportCode(dto.getJasperReportCode())
                    .status(dto.getStatus())
                    .errorMessage(dto.getErrorMessage())
                    .createdAt(dto.getCreatedAt())
                    .updatedAt(dto.getUpdatedAt())
                    .reportFormatType(dto.getReportFormatType())
                    .fileSizeBytes(dto.getFileSizeBytes())
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response));
        } else {
            ErrorResponse error = new ErrorResponse("Report not found", "No report found with serverRequestId: " + serverRequestId);
            return ResponseEntity.status(404).body(ApiResponse.error(error));
        }
    }

    @GetMapping("/download/{serverRequestId}")
    public ResponseEntity<ApiResponse<ReportDownloadResponse>> downloadReport(@PathVariable String serverRequestId) {
        Optional<ReportProcessingDto> tracking = reportTrackingService.getReportTracking(serverRequestId);

        if (tracking.isPresent()) {
            ReportProcessingDto report = tracking.get();
            if (report.getStatus() == ReportStatus.COMPLETED && report.getReportBase64() != null) {

                ReportDownloadResponse response = ReportDownloadResponse.builder()
                        .base64Report(report.getReportBase64())
                        .fileSizeBytes(report.getFileSizeBytes())
                        .fileName(generateFileName(report))
                        .contentType(getContentType(report.getReportFormatType()))
                        .build();

                return ResponseEntity.ok(ApiResponse.success(response));
            } else {
                ErrorResponse error = new ErrorResponse("Report not ready", "Current status: " + report.getStatus());
                return ResponseEntity.badRequest().body(ApiResponse.error(error));
            }
        } else {
            ErrorResponse error = new ErrorResponse("Report not found", "No report found with serverRequestId: " + serverRequestId);
            return ResponseEntity.status(404).body(ApiResponse.error(error));
        }
    }

    @DeleteMapping("/cleanup/{serverRequestId}")
    public ResponseEntity<ApiResponse<SimpleResponse>> cleanupReport(@PathVariable String serverRequestId) {
        reportTrackingService.deleteReportTracking(serverRequestId);
        SimpleResponse response = new SimpleResponse("Report data cleaned up successfully");
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private String generateFileName(ReportProcessingDto report) {
        String extension = switch (report.getReportFormatType().toUpperCase()) {
            case "PDF" -> ".pdf";
            case "XLS", "XLSX" -> ".xlsx";
            case "CSV" -> ".csv";
            default -> ".pdf";
        };
        return report.getJasperReportCode() + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                extension;
    }

    private String getContentType(String reportFormatType) {
        return switch (reportFormatType.toUpperCase()) {
            case "PDF" -> "application/pdf";
            case "XLS", "XLSX" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "CSV" -> "text/csv";
            default -> "application/octet-stream";
        };
    }

    public ResponseEntity<String> executeReportInternal(GenerateTemplateRequest request) {
        long startTime = System.currentTimeMillis();
        logger.info("Report execution started | Request ID: {} | Timestamp: {}", request.getRequestId(), request.getMetadata() != null
                ? request.getMetadata().getTimestamp() : "N/A");
        Connection connection = null;
        try {
            JasperReportTemplateDto reportTemplateDto = reportService.findByTemplateCode(request.getJasperReportCode());
            logger.info("Report code: {}", request.getJasperReportCode());
            logger.info("Report format type: {}", request.getReportFormatType());
            logger.info("Parameters: {}", request.getParameters());

            // Use extracted parameter conversion method
            Map<String, Object> convertedParams = convertParameters(request.getParameters());
            logger.info("Final converted parameters: {}", convertedParams);
            // If GenerateTemplateRequest has getMetadata, log metadata
            try {
                java.lang.reflect.Method getMetadataMethod = request.getClass().getMethod("getMetadata");
                Object metadata = getMetadataMethod.invoke(request);
                logger.info("Request metadata: {}", metadata);
            } catch (NoSuchMethodException ex) {
                // Ignore if doesn't exist
            } catch (Exception ex) {
                logger.warn("Error obtaining request metadata: {}", ex.getMessage());
            }
            logger.info("Database URL: {}", reportTemplateDto.getDbConectionDto().getUrl());

            // Cargar el archivo JRXML
            JasperReport jasperReport = loadJasperReportFromUrl(reportTemplateDto.getFile());
            if (jasperCache.containsKey(reportTemplateDto.getFile())) {
                logger.info("Template JRXML loaded from cache: {}", reportTemplateDto.getFile());
            }

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
                long endTime = System.currentTimeMillis();
                logger.info("Report execution completed successfully | Request ID: {} | Duration: {} ms", request.getRequestId(), (endTime - startTime));
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{\"base64Report\": \"" + base64Report + "\"}");
            }
        } catch (Exception e) {
            long errorTime = System.currentTimeMillis();
            logger.error("Report execution failed | Request ID: {} | Duration: {} ms", request.getRequestId(), (errorTime - startTime));
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
                    logger.warn("Parameter {} with value '{}' could not be converted to java.sql.Date", key, value);
                    convertedParams.put(key, value);
                }
            } else if (value instanceof List<?>) {
                List<?> list = (List<?>) value;
                String joined = String.join(",", list.stream().map(Object::toString).toArray(String[]::new));
                convertedParams.put(key, joined);
            } else {
                convertedParams.put(key, value);
            }

            logger.info("Converting parameter: {} | Original type: {} | Original value: {} | Converted value: {}",
                key,
                value != null ? value.getClass().getSimpleName() : "null",
                value,
                convertedParams.get(key));
        }
        return convertedParams;
    }

    private JasperReport loadJasperReportFromUrl(String templateUrl) {
        try {
            JasperReport report = jasperCache.computeIfAbsent(templateUrl, url -> {
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
            if (jasperCache.containsKey(templateUrl)) {
                logger.info("Template JRXML loaded from cache: {}", templateUrl);
            }
            return report;
        } catch (RuntimeException e) {
            throw e;
        }
    }
}

