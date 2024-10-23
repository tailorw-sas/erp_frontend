package com.kynsoft.finamer.creditcard.application.query.managerAccountType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManagerAccountTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerAccountTypeQueryHandler implements IQueryHandler<GetSearchManagerAccountTypeQuery, PaginatedResponse> {
    private final IManagerAccountTypeService service;

    public GetSearchManagerAccountTypeQueryHandler(IManagerAccountTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerAccountTypeQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
