package com.kynsoft.finamer.invoicing.application.query.manageContact.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.infrastructure.services.IManageContactService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageContactQueryHandler implements IQueryHandler<GetSearchManageContactQuery, PaginatedResponse> {
    private final IManageContactService service;
    
    public GetSearchManageContactQueryHandler(IManageContactService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageContactQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
