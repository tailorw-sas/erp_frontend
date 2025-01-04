package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.query.collections.invoice.InvoiceCollectionsSummaryQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/collections")
public class CollectionsController {

    private final IMediator mediator;

    public CollectionsController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/invoice-summary")
    public ResponseEntity<?> invoiceCollectionSummary(@RequestBody SearchRequest searchRequest){
        InvoiceCollectionsSummaryQuery query =new InvoiceCollectionsSummaryQuery(searchRequest.getFilter(), PageableUtil.createPageable(searchRequest));
        return ResponseEntity.ok(mediator.send(query));
    }
}
