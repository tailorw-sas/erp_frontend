package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.query.collections.CollectionsSummaryQuery;
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

    @PostMapping("/summary")
    public ResponseEntity<?> collectionSummary(@RequestBody SearchRequest searchRequest){
        CollectionsSummaryQuery query =new CollectionsSummaryQuery(searchRequest.getFilter());
        return mediator.send(query);
    }
}
