package com.kynsoft.report.controller;

import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateCommand;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateMessage;
import com.kynsoft.report.applications.command.generateTemplate.GenerateTemplateRequest;
import com.kynsoft.report.applications.query.reportTemplate.GetReportParameterByCodeQuery;
import com.kynsoft.report.applications.query.reportTemplate.GetReportParameterByCodeResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final IMediator mediator;

    public ReportController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping(value = "/generate-template", produces = {MediaType.APPLICATION_PDF_VALUE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"})
    public ResponseEntity<?> getTemplate(@RequestBody GenerateTemplateRequest request) {
        try {
            GenerateTemplateCommand command = new GenerateTemplateCommand(request.getParameters(),
                    request.getJasperReportCode(), request.getReportFormatType());
            GenerateTemplateMessage response = mediator.send(command);

            // Verificar si el resultado es nulo o vacío
            if (response == null || response.getResult() == null || response.getResult().length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el reporte.");
            }

            String contentType;
            String fileName;
            if ("XLS".equalsIgnoreCase(request.getReportFormatType())) {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                fileName = "report.xlsx";
            } else {
                contentType = MediaType.APPLICATION_PDF_VALUE;
                fileName = "report.pdf";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(response.getResult());
        } catch (Exception e) {
            // Manejar errores y responder adecuadamente
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud.");
        }
    }

//    @PostMapping(value = "/generate-template", produces = MediaType.TEXT_PLAIN_VALUE)
//    public ResponseEntity<String> getTemplate(@RequestBody GenerateTemplateRequest request) {
//
//        GenerateTemplateCommand command = new GenerateTemplateCommand(request.getParameters(),
//                request.getJasperReportCode(), request.getReportFormatType());
//        GenerateTemplateMessage response = mediator.send(command);
//
//        // Retornar el resultado como texto
//        return ResponseEntity.ok()
//                .contentType(MediaType.TEXT_PLAIN)
//                .body("test");
//
//    }

    @GetMapping("/parameters/{reportCode}")
    public ResponseEntity<?> getReportParameters(@PathVariable String reportCode) {
        GetReportParameterByCodeQuery query = new GetReportParameterByCodeQuery(reportCode);
        GetReportParameterByCodeResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

}

