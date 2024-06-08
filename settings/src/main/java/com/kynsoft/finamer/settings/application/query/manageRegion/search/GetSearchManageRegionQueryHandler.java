package com.kynsoft.finamer.settings.application.query.manageRegion.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageRegionService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageRegionQueryHandler implements IQueryHandler<GetSearchManageRegionQuery, PaginatedResponse> {

    private final IManageRegionService service;

    public GetSearchManageRegionQueryHandler(IManageRegionService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageRegionQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
