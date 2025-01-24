package com.tailorw.finamer.scheduler.application.query.businessProcessSchedulerRules.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerRuleService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchBusinessProcessSchedulerRuleQueryHandler implements IQueryHandler<GetSearchBusinessProcessSchedulerRuleQuery, PaginatedResponse> {

    private final IBusinessProcessSchedulerRuleService businessProcessSchedulerRuleService;

    public GetSearchBusinessProcessSchedulerRuleQueryHandler(IBusinessProcessSchedulerRuleService businessProcessSchedulerRuleService){
        this.businessProcessSchedulerRuleService = businessProcessSchedulerRuleService;
    }

    @Override
    public PaginatedResponse handle(GetSearchBusinessProcessSchedulerRuleQuery query) {
        return businessProcessSchedulerRuleService.search(query.getPageable(), query.getFilter());
    }
}
