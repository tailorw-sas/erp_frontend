package com.kynsoft.finamer.payment.application.query.manageResourceType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IManageResourceTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageResourceTypeQueryHandler implements IQueryHandler<GetSearchManageResourceTypeQuery, PaginatedResponse> {
    private final IManageResourceTypeService service;
    
    public GetSearchManageResourceTypeQueryHandler(IManageResourceTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageResourceTypeQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
