package com.kynsoft.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateCommand;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateMessage;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateRequest;
import com.kynsoft.report.applications.query.reportTemplate.GetReportParameterByCodeQuery;
import com.kynsoft.report.applications.query.reportTemplate.GetReportParameterByCodeResponse;
import com.kynsoft.report.domain.dto.*;
import com.kynsoft.report.domain.enums.ReportStatus;
import com.kynsoft.report.domain.events.ReportProcessingEvent;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import com.kynsoft.report.domain.services.IReportCleanupService;
import com.kynsoft.report.infrastructure.enums.ReportFormatType;
import com.kynsoft.report.infrastructure.messaging.ReportEventProducer;
import com.kynsoft.report.infrastructure.services.ReportS3Service;
import com.kynsoft.report.infrastructure.services.ReportTrackingService;
import com.kynsoft.report.infrastructure.utils.ReportS3ConfigurationProperties;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final IMediator mediator;
    private final IJasperReportTemplateService reportService;
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final ConcurrentHashMap<String, JasperReport> jasperCache = new ConcurrentHashMap<>();
    private final AtomicInteger compileCounter = new AtomicInteger(0);
    private final ReportS3Service reportS3Service;
    private final ReportS3ConfigurationProperties s3Config;

    @Autowired
    private ReportEventProducer reportEventProducer;

    @Autowired
    private ReportTrackingService reportTrackingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IReportCleanupService cleanupService;

    public ReportController(IMediator mediator, IJasperReportTemplateService reportService, ReportS3Service reportS3Service,
                            ReportS3ConfigurationProperties s3Config) {
        this.mediator = mediator;
        this.reportService = reportService;
        this.reportS3Service = reportS3Service;
        this.s3Config = s3Config;
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

            if (report.getStatus() == ReportStatus.COMPLETED) {

                // ✅ NUEVO: Manejo híbrido según storage method
                if (report.isUseS3Storage()) {
                    // ✅ S3 Storage - Verificar si URL sigue válida
                    if (report.getS3ExpirationDate() != null &&
                            report.getS3ExpirationDate().isBefore(LocalDateTime.now())) {

                        // URL expirada - regenerar si es posible
                        String newPresignedUrl = regeneratePresignedUrlIfPossible(report);
                        if (newPresignedUrl != null) {
                            report.setS3PreSignedUrl(newPresignedUrl);
                            report.setS3ExpirationDate(LocalDateTime.now().plusHours(s3Config.getPresignedUrlExpiryHours()));
                            reportTrackingService.saveTracking(report);
                        } else {
                            ErrorResponse error = new ErrorResponse("Report expired", "S3 download URL has expired and cannot be regenerated");
                            return ResponseEntity.badRequest().body(ApiResponse.error(error));
                        }
                    }

                    // ✅ Retornar URL de S3
                    ReportDownloadResponse response = ReportDownloadResponse.builder()
                            .downloadUrl(report.getS3PreSignedUrl())  // ✅ NUEVO CAMPO
                            .fileSizeBytes(report.getFileSizeBytes())
                            .fileName(generateFileName(report))
                            .contentType(getContentType(report.getReportFormatType()))
                            .storageMethod("S3")  // ✅ NUEVO CAMPO
                            .expirationDate(report.getS3ExpirationDate())  // ✅ NUEVO CAMPO
                            .build();

                    return ResponseEntity.ok(ApiResponse.success(response));

                } else {
                    // ✅ Base64 Storage (fallback o legacy)
                    if (report.getReportBase64() == null) {
                        ErrorResponse error = new ErrorResponse("Report data missing", "Report was processed but data is not available");
                        return ResponseEntity.badRequest().body(ApiResponse.error(error));
                    }

                    ReportDownloadResponse response = ReportDownloadResponse.builder()
                            .base64Report(report.getReportBase64())  // ✅ MANTENER PARA COMPATIBILIDAD
                            .fileSizeBytes(report.getFileSizeBytes())
                            .fileName(generateFileName(report))
                            .contentType(getContentType(report.getReportFormatType()))
                            .storageMethod("BASE64")  // ✅ NUEVO CAMPO
                            .build();

                    return ResponseEntity.ok(ApiResponse.success(response));
                }

            } else {
                ErrorResponse error = new ErrorResponse("Report not ready", "Current status: " + report.getStatus());
                return ResponseEntity.badRequest().body(ApiResponse.error(error));
            }
        } else {
            ErrorResponse error = new ErrorResponse("Report not found", "No report found with serverRequestId: " + serverRequestId);
            return ResponseEntity.status(404).body(ApiResponse.error(error));
        }
    }

    // ✅ NUEVO: Intentar regenerar URL presignada
    private String regeneratePresignedUrlIfPossible(ReportProcessingDto report) {
        try {
            if (report.getS3ObjectKey() != null && reportS3Service.objectExists(report.getS3ObjectKey())) {
                return reportS3Service.generatePreSignedDownloadUrl(
                        report.getS3ObjectKey(),
                        Duration.ofHours(s3Config.getPresignedUrlExpiryHours())
                );
            }
        } catch (Exception e) {
            logger.warn("Could not regenerate presigned URL for report | ServerID: {} | S3Key: {} | Error: {}",
                    report.getServerRequestId(), report.getS3ObjectKey(), e.getMessage());
        }
        return null;
    }
    @DeleteMapping("/cleanup/{serverRequestId}")
    public ResponseEntity<ApiResponse<SimpleResponse>> cleanupReport(@PathVariable String serverRequestId) {
        reportTrackingService.deleteReportTracking(serverRequestId);
        SimpleResponse response = new SimpleResponse("Report data cleaned up successfully");
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/admin/cleanup/{date}")
    public ResponseEntity<ApiResponse<CleanupResult>> manualCleanup(@PathVariable String date) {
        try {
            LocalDate cleanupDate = LocalDate.parse(date);
            CleanupResult result = cleanupService.cleanupReportsForDate(cleanupDate);

            if (result.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success(result));
            } else {
                return ResponseEntity.status(500).body(ApiResponse.error(
                        new ErrorResponse("Cleanup failed", result.getErrorMessage())
                ));
            }
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Invalid date or cleanup error", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(error));
        }
    }

    @GetMapping("/admin/cleanup/preview/{date}")
    public ResponseEntity<ApiResponse<CleanupPreviewResult>> previewCleanup(@PathVariable String date) {
        try {
            LocalDate cleanupDate = LocalDate.parse(date);
            CleanupPreviewResult result = cleanupService.previewCleanup(cleanupDate);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Preview failed", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(error));
        }
    }

    @PostMapping("/admin/cleanup/range")
    public ResponseEntity<ApiResponse<CleanupResult>> cleanupDateRange(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            CleanupResult result = cleanupService.cleanupReportsForDateRange(start, end);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Range cleanup failed", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(error));
        }
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

            // Log metadata if available
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

            byte[] reportBytes;
            String contentType;
            String fileName;

            // Use try-with-resources for ByteArrayOutputStream
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
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
                        contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                        fileName = generateFileName(request.getJasperReportCode(), "xlsx");
                        break;
                    case CSV:
                        net.sf.jasperreports.engine.export.JRCsvExporter exporterCsv = new net.sf.jasperreports.engine.export.JRCsvExporter();
                        exporterCsv.setExporterInput(new SimpleExporterInput(jasperPrint));
                        exporterCsv.setExporterOutput(new net.sf.jasperreports.export.SimpleWriterExporterOutput(outputStream));
                        exporterCsv.exportReport();
                        contentType = "text/csv";
                        fileName = generateFileName(request.getJasperReportCode(), "csv");
                        break;
                    case PDF:
                    default:
                        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
                        contentType = "application/pdf";
                        fileName = generateFileName(request.getJasperReportCode(), "pdf");
                        break;
                }

                reportBytes = outputStream.toByteArray();
            }

            ReportGenerationResponse response = attemptS3UploadWithFallback(request.getRequestId(), reportBytes, fileName, contentType);

            long endTime = System.currentTimeMillis();
            logger.info("Report execution completed successfully | Request ID: {} | Duration: {} ms | Storage: {}",
                    request.getRequestId(), (endTime - startTime), response.getStorageMethod());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(response));

        } catch (Exception e) {
            long errorTime = System.currentTimeMillis();
            logger.error("Report execution failed | Request ID: {} | Duration: {} ms", request.getRequestId(), (errorTime - startTime));
            logger.error("Report code: {}", request.getJasperReportCode(), e);
            return ResponseEntity.status(500)
                    .body("{\"error\": \"Error generating report\", \"details\": \"" +
                    e.getMessage().replace("\"", "\\\"") + "\"}");
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

    private ReportGenerationResponse attemptS3UploadWithFallback(String serverRequestId, byte[] reportBytes, String fileName, String contentType) {

        // ✅ Verificar si S3 está habilitado y disponible
        if (!s3Config.isFallbackToBase64() || !reportS3Service.isS3Available()) {
            logger.warn("S3 not available or disabled, using base64 fallback | ServerRequestId: {}", serverRequestId);
            return createBase64Response(reportBytes, fileName, contentType, "S3_DISABLED");
        }

        // ✅ Intentar upload a S3
        try {
            logger.info("Attempting S3 upload | ServerRequestId: {} | FileSize: {} bytes",
                    serverRequestId, reportBytes.length);

            ReportS3UploadResult s3Result = reportS3Service.uploadReportToS3(serverRequestId, reportBytes, fileName, contentType);

            if (s3Result.isSuccess()) {
                logger.info("S3 upload successful | ServerRequestId: {} | S3Key: {}",
                        serverRequestId, s3Result.getS3ObjectKey());

                return ReportGenerationResponse.builder()
                        .useS3Storage(true)
                        .s3DownloadUrl(s3Result.getPreSignedUrl())
                        .fileName(fileName)
                        .contentType(contentType)
                        .fileSizeBytes((long) reportBytes.length)
                        .expirationDate(s3Result.getExpirationDate())
                        .storageMethod("S3")
                        .build();
            } else {
                logger.warn("S3 upload failed, falling back to base64 | ServerRequestId: {} | Error: {}",
                        serverRequestId, s3Result.getErrorMessage());

                return createBase64Response(reportBytes, fileName, contentType,
                        "S3_FAILED: " + s3Result.getErrorMessage());
            }

        } catch (Exception e) {
            logger.error("Unexpected error during S3 upload, falling back to base64 | ServerRequestId: {}",
                    serverRequestId, e);

            return createBase64Response(reportBytes, fileName, contentType,
                    "S3_ERROR: " + e.getMessage());
        }
    }

    private ReportGenerationResponse createBase64Response(byte[] reportBytes, String fileName, String contentType, String errorReason) {
        String base64Report = Base64.getEncoder().encodeToString(reportBytes);

        return ReportGenerationResponse.builder()
                .useS3Storage(false)
                .base64Report(base64Report)
                .fileName(fileName)
                .contentType(contentType)
                .fileSizeBytes((long) reportBytes.length)
                .storageMethod("BASE64")
                .errorMessage(errorReason)
                .build();
    }

    private String generateFileName(String reportCode, String extension) {
        return String.format("%s_%s.%s",
                reportCode,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")),
                extension
        );
    }
}

