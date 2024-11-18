package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.query.manageInvoice.sendAccountStatement.SendAccountStatementQuery;
import com.kynsoft.finamer.invoicing.application.query.manageInvoice.sendAccountStatement.SendAccountStatementRequest;
import com.kynsoft.finamer.invoicing.application.query.manageInvoice.sendAccountStatement.SendAccountStatementResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment/manage-invoice")
public class ManageInvoiceBalanceController {

    private final IMediator mediator;

    public ManageInvoiceBalanceController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping("/send-account-statement")
    public ResponseEntity<?> sendAccountStatement(@RequestBody SendAccountStatementRequest request) {

        SendAccountStatementQuery query = new SendAccountStatementQuery(request.getInvoiceIds(), request.getEmployee());
        SendAccountStatementResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
