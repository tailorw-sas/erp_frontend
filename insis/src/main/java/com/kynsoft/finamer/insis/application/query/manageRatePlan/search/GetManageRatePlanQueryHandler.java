package com.kynsoft.finamer.insis.application.query.manageRatePlan.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.services.IManageRatePlanService;
import org.springframework.stereotype.Component;

@Component
public class GetManageRatePlanQueryHandler implements IQueryHandler<GetManageRatePlanQuery, PaginatedResponse> {

    private final IManageRatePlanService service;

    public GetManageRatePlanQueryHandler(IManageRatePlanService service){
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetManageRatePlanQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
