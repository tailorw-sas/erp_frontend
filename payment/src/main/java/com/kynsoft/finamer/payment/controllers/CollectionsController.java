package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.query.collections.payment.PaymentCollectionsSummaryQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/collections")
public class CollectionsController {

    private final IMediator mediator;

    public CollectionsController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/payment-summary")
    public ResponseEntity<?> paymentCollectionSummary(@AuthenticationPrincipal Jwt jwt, @RequestBody SearchRequest searchRequest){
        String userId = jwt.getClaim("sub");
        UUID employeeId = UUID.fromString(userId);
        PaymentCollectionsSummaryQuery query = new PaymentCollectionsSummaryQuery(searchRequest.getFilter(),PageableUtil.createPageable(searchRequest), employeeId);
        return mediator.send(query);
    }
}
