package com.kynsoft.finamer.creditcard.application.query.manageAgency.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageAgencyService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageAgencyQueryHandler implements IQueryHandler<GetSearchManageAgencyQuery, PaginatedResponse> {

    private final IManageAgencyService service;

    public GetSearchManageAgencyQueryHandler(IManageAgencyService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageAgencyQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
