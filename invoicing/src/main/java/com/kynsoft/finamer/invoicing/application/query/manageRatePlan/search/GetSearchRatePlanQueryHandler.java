package com.kynsoft.finamer.invoicing.application.query.manageRatePlan.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchRatePlanQueryHandler implements IQueryHandler<GetSearchManageRatePlanQuery, PaginatedResponse> {

    private final IManageRatePlanService service;

    public GetSearchRatePlanQueryHandler(IManageRatePlanService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageRatePlanQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
