package com.tailorw.finamer.scheduler.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.finamer.scheduler.application.query.businessProcessSchedulerRules.search.GetSearchBusinessProcessSchedulerRuleQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/business-process-scheduler-rule")
public class BusinessProcessSchedulerRuleController {

    private final IMediator mediator;

    public BusinessProcessSchedulerRuleController(IMediator mediator){
        this.mediator = mediator;
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request){
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchBusinessProcessSchedulerRuleQuery query = new GetSearchBusinessProcessSchedulerRuleQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return  ResponseEntity.ok(response);
    }
}
