package com.kynsoft.report.controller;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

@RestController
@RequestMapping("/api/reports-test")
public class ReportTestController {

    private final IMediator mediator;
     private final IJasperReportTemplateService reportService;
    private static final Logger logger = LoggerFactory.getLogger(ReportTestController.class);

    public ReportTestController(IMediator mediator
                            , IJasperReportTemplateService reportService
    ) {
        this.mediator = mediator;
         this.reportService = reportService;
    }

//    @PostMapping(value = "/generate/template-test")
//    public ResponseEntity<?> generatePdfReportTest(@RequestBody ReportRequest reportRequest) {
//        try {
//            //JasperReportTemplateDto reportTemplateDto = reportService.findByTemplateCode(reportRequest.getReportCode());
//            // Crear un DataSource personalizado con los datos de conexión
//            logger.info("Report code: {}", reportRequest.getReportCode());
//            logger.info("Database URL: {}", "jdbc:postgresql://172.20.41.100:5432/finamer-payments");
//            logger.info("Parameters: {}", reportRequest.getParameters());
//
//            DriverManagerDataSource customDataSource = new DriverManagerDataSource();
//            customDataSource.setDriverClassName("org.postgresql.Driver"); // Cambia el driver según tu base de datos
//
//            // customDataSource.setUrl(reportTemplateDto.getDbConectionDto().getUrl()); // URL de tu base de datos
//            // customDataSource.setUrl("jdbc:postgresql://172.20.41.100:5432/finamer-payments"); // URL de tu base de datos
//            customDataSource.setUrl("jdbc:postgresql://postgres-erp-rw.postgres.svc.cluster.local:5432/finamer-payments");
//            customDataSource.setUsername("finamer_rw"); // Usuario
//            customDataSource.setPassword("5G30y1cXz89cA1yc0gCE3OhhBLQkvUTV2icqz5qNRQGq4cbM5F0bc"); // Contraseña
//
//
//            // Cargar el archivo JRXML desde la URL proporcionada
//            JasperReport jasperReport = loadJasperReportFromUrl("https://static.kynsoft.net/Blank_A4_2024-11-08_17-00-36.jrxml");
//            // JasperReport jasperReport = loadJasperReportFromUrl(reportTemplateDto.getFile());
//
//            // Llenar el reporte con la fuente de datos y parámetros
//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reportRequest.getParameters(), customDataSource.getConnection());
//
//            // Exportar a PDF
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
//
//            // Preparar la respuesta HTTP
//            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-Disposition", "inline; filename=report.pdf");
//
//            return ResponseEntity.ok()
//                    .body(new InputStreamResource(inputStream));
//        } catch (Exception e) {
//            logger.error("Error: {}", e.getMessage());
//            throw new RuntimeException("Error generating report", e);
//        }
//    }
//
//
//    @PostMapping(value = "/generate-template", produces = {MediaType.APPLICATION_PDF_VALUE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})
//    public ResponseEntity<?> getTemplate(@RequestBody GenerateTemplateRequest request) {
//        try {
//            GenerateTemplateCommand command = new GenerateTemplateCommand(request.getParameters(),
//                    request.getJasperReportCode(), request.getReportFormatType());
//            GenerateTemplateMessage response = mediator.send(command);
//
//            // Verificar si el resultado es nulo o vacío
//            if (response == null || response.getResult() == null || response.getResult().length == 0) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el reporte.");
//            }
//
//            String contentType;
//            String fileName;
//            if ("XLS".equalsIgnoreCase(request.getReportFormatType())) {
//                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
//                fileName = "report.xlsx";
//            } else {
//                contentType = MediaType.APPLICATION_PDF_VALUE;
//                fileName = "report.pdf";
//            }
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
//                    .contentType(MediaType.parseMediaType(contentType))
//                    .body(response.getResult());
//        } catch (Exception e) {
//            // Manejar errores y responder adecuadamente
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud.");
//        }
//    }
//
//    @GetMapping("/parameters/{reportCode}")
//    public ResponseEntity<?> getReportParameters(@PathVariable String reportCode) {
//        GetReportParameterByCodeQuery query = new GetReportParameterByCodeQuery(reportCode);
//        GetReportParameterByCodeResponse response = mediator.send(query);
//        return ResponseEntity.ok(response);
//    }


    //Prueba con Jose
    @PostMapping(value = "/generate/template", produces = { MediaType.APPLICATION_PDF_VALUE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" })
    public ResponseEntity<InputStreamResource> generateReport(@RequestBody GenerateTemplateRequest request) {
        try {
            logger.info("Report code: {}", request.getJasperReportCode());
            logger.info("Report format type: {}", request.getReportFormatType());
            logger.info("Parameters: {}", request.getParameters());
            JasperReportTemplateDto reportTemplateDto = reportService.findByTemplateCode(request.getJasperReportCode());
            // Cargar el archivo JRXML desde la URL (por simplicidad, se utiliza un archivo local o una URL conocida)
            JasperReport jasperReport = loadJasperReportFromUrl("https://static.kynsoft.net/Copia_de_Blank_A4_2024-11-09_11-48-00.jrxml");

            // Llenar el reporte con los parámetros y un JREmptyDataSource
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, request.getParameters(), new JREmptyDataSource());

            // Crear el archivo de salida según el tipo de formato
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            String contentType;
            String fileName;

            if ("XLS".equalsIgnoreCase(request.getReportFormatType())) {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                fileName = "report.xlsx";

                // Configurar el exportador de Excel
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

            // Preparar la respuesta HTTP
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            throw new RuntimeException("Error generating report", e);
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
