package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.http.entity.InvoiceHttp;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.query.http.invoice.invoice.uuid.FindInvoiceByUUIDQuery;
import com.kynsoft.finamer.payment.application.query.manageInvoice.search.GetSearchManageInvoiceQuery;
import com.kynsoft.finamer.payment.application.query.manageInvoice.sendAccountStatement.SendAccountStatementQuery;
import com.kynsoft.finamer.payment.application.query.manageInvoice.sendAccountStatement.SendAccountStatementRequest;
import com.kynsoft.finamer.payment.application.query.manageInvoice.sendAccountStatement.SendAccountStatementResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment/manage-invoice")
public class ManageInvoiceController {

    private final IMediator mediator;

    public ManageInvoiceController(IMediator mediator) {

        this.mediator = mediator;
    }
    
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageInvoiceQuery query = new GetSearchManageInvoiceQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @GetMapping(path = "/uuid/{id}")
    public ResponseEntity<?> getByUUID(@PathVariable UUID id) {

        FindInvoiceByUUIDQuery query = new FindInvoiceByUUIDQuery(id, mediator);
        InvoiceHttp response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-account-statement")
    public ResponseEntity<?> sendAccountStatement(@RequestBody SendAccountStatementRequest request) {


        SendAccountStatementQuery query = new SendAccountStatementQuery(request.getInvoiceIds());
        SendAccountStatementResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
