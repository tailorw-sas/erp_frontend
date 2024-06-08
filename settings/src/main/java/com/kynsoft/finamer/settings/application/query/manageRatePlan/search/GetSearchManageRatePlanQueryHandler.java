package com.kynsoft.finamer.settings.application.query.manageRatePlan.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageRatePlanService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageRatePlanQueryHandler implements IQueryHandler<GetSearchManageRatePlanQuery, PaginatedResponse> {

    private final IManageRatePlanService service;

    public GetSearchManageRatePlanQueryHandler(final IManageRatePlanService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageRatePlanQuery query) {
        return this.service.search(query.getPageable(), query.getFilter());
    }
}
