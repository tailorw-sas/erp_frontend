package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.query.collections.invoice.InvoiceCollectionsSummaryQuery;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public ResponseEntity<?> invoiceCollectionSummary(@AuthenticationPrincipal Jwt jwt, @RequestBody SearchRequest searchRequest){
        
        String userId = jwt.getClaim("sub");
        UUID employeeId = UUID.fromString(userId);
//        UUID employeeId = UUID.fromString("637ee5cb-1e36-4917-a0a9-5874bc8bea04");
        InvoiceCollectionsSummaryQuery query =new InvoiceCollectionsSummaryQuery(searchRequest.getFilter(), PageableUtil.createPageable(searchRequest), employeeId);
        return ResponseEntity.ok(mediator.send(query));
    }
}
