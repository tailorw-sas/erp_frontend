package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.query.report.InvoiceReportQuery;
import com.kynsoft.finamer.invoicing.application.query.report.InvoiceReportRequest;
import com.kynsoft.finamer.invoicing.application.query.report.InvoiceReportResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

@RestController
@RequestMapping("/api/manage-invoice")
public class InvoiceReportController {

    private final IMediator mediator;

    public InvoiceReportController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping(value = "/report")
    public ResponseEntity<?> print(@RequestBody InvoiceReportRequest invoiceReportRequest){
        InvoiceReportQuery invoiceReportQuery = new InvoiceReportQuery(invoiceReportRequest);
        InvoiceReportResponse invoiceReportResponse = mediator.send(invoiceReportQuery);
        ByteArrayOutputStream file = invoiceReportResponse.getFile();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+ invoiceReportResponse.getFileName());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        return ResponseEntity.ok().headers(headers).body(Objects.nonNull(file)?file.toByteArray():null);
    }
}
