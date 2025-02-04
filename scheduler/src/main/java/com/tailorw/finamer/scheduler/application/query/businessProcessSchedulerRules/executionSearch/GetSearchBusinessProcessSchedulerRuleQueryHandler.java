package com.tailorw.finamer.scheduler.application.query.businessProcessSchedulerRules.executionSearch;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerExecutionRuleService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchBusinessProcessSchedulerRuleQueryHandler implements IQueryHandler<GetSearchBusinessProcessSchedulerRuleQuery, PaginatedResponse> {

    private final IBusinessProcessSchedulerExecutionRuleService businessProcessSchedulerRuleService;

    public GetSearchBusinessProcessSchedulerRuleQueryHandler(IBusinessProcessSchedulerExecutionRuleService businessProcessSchedulerRuleService){
        this.businessProcessSchedulerRuleService = businessProcessSchedulerRuleService;
    }

    @Override
    public PaginatedResponse handle(GetSearchBusinessProcessSchedulerRuleQuery query) {
        return businessProcessSchedulerRuleService.search(query.getPageable(), query.getFilter());
    }
}
