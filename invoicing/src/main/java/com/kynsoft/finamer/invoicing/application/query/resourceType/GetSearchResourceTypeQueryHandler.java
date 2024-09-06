package com.kynsoft.finamer.invoicing.application.query.resourceType;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageResourceTypeService;

public class GetSearchResourceTypeQueryHandler implements IQueryHandler<GetSearchResourceTypeQuery,PaginatedResponse> {

    private final IManageResourceTypeService resourceTypeService;

    public GetSearchResourceTypeQueryHandler(IManageResourceTypeService resourceTypeService) {
        this.resourceTypeService = resourceTypeService;
    }

    @Override
    public PaginatedResponse handle(GetSearchResourceTypeQuery query) {
        return resourceTypeService. search(query.getPageable(),query.getFilter());
    }
}
