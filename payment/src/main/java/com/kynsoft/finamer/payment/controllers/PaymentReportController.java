package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.query.report.PaymentReportQuery;
import com.kynsoft.finamer.payment.application.query.report.PaymentReportRequest;
import com.kynsoft.finamer.payment.application.query.report.PaymentReportResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/payment")
public class PaymentReportController {

    private final IMediator mediator;

    public PaymentReportController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping(value = "/report")
    public ResponseEntity<?> print(@RequestBody PaymentReportRequest paymentReportRequest){
        PaymentReportQuery paymentReportQuery = new PaymentReportQuery(paymentReportRequest);
        PaymentReportResponse paymentReportResponse = mediator.send(paymentReportQuery);
        ByteArrayOutputStream file = paymentReportResponse.getFile();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+ paymentReportResponse.getFileName());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        return ResponseEntity.ok().headers(headers).body(file.toByteArray());
    }
}
