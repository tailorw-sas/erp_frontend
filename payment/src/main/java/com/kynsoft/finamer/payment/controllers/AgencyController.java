package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.query.manageAgency.search.GetSearchAgencyQuery;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agency")
public class AgencyController {

    private final IMediator mediator;

    public AgencyController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchAgencyQuery query = new GetSearchAgencyQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
