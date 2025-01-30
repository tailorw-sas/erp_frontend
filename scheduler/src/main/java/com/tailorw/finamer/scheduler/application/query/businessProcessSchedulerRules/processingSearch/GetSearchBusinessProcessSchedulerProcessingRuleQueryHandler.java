package com.tailorw.finamer.scheduler.application.query.businessProcessSchedulerRules.processingSearch;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerProcessingRuleService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchBusinessProcessSchedulerProcessingRuleQueryHandler implements IQueryHandler<GetSearchBusinessProcessSchedulerProcessingRuleQuery, PaginatedResponse> {

    private final IBusinessProcessSchedulerProcessingRuleService businessProcessSchedulerProcessingRuleService;

    public GetSearchBusinessProcessSchedulerProcessingRuleQueryHandler(IBusinessProcessSchedulerProcessingRuleService businessProcessSchedulerProcessingRuleService){
        this.businessProcessSchedulerProcessingRuleService = businessProcessSchedulerProcessingRuleService;
    }

    @Override
    public PaginatedResponse handle(GetSearchBusinessProcessSchedulerProcessingRuleQuery query) {
        return businessProcessSchedulerProcessingRuleService.search(query.getPageable(), query.getFilter());
    }
}
