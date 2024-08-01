package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;


import com.kynsoft.finamer.invoicing.application.query.invoiceStatusHistory.getById.FindInvoiceStatusHistoryByIdQuery;
import com.kynsoft.finamer.invoicing.application.query.invoiceStatusHistory.search.GetSearchInvoiceStatusHistoryQuery;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.InvoiceStatusHistoryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/invoice-status-history")
public class InvoiceStatusHistoryController {

    private final IMediator mediator;

    public InvoiceStatusHistoryController(IMediator mediator) {
        this.mediator = mediator;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindInvoiceStatusHistoryByIdQuery query = new FindInvoiceStatusHistoryByIdQuery(id);
        InvoiceStatusHistoryResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchInvoiceStatusHistoryQuery query = new GetSearchInvoiceStatusHistoryQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
