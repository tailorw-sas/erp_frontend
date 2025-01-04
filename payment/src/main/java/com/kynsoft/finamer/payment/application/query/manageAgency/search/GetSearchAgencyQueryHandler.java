package com.kynsoft.finamer.payment.application.query.manageAgency.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchAgencyQueryHandler implements IQueryHandler<GetSearchAgencyQuery, PaginatedResponse> {
    private final IManageAgencyService service;

    public GetSearchAgencyQueryHandler(IManageAgencyService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchAgencyQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
