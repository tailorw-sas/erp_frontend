package com.kynsoft.finamer.creditcard.application.query.resourceType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageResourceTypeService;
import org.springframework.stereotype.Component;

@Component
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
