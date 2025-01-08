package com.kynsoft.finamer.insis.application.query.manageAgency.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.services.IManageAgencyService;
import org.springframework.stereotype.Component;

@Component
public class GetManageAgencyQueryHandler implements IQueryHandler<GetManageAgencyQuery, PaginatedResponse> {

    private final IManageAgencyService service;

    public GetManageAgencyQueryHandler(IManageAgencyService service){
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetManageAgencyQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
