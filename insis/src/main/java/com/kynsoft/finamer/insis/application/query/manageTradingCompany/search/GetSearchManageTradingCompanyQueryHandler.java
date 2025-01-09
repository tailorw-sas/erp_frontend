package com.kynsoft.finamer.insis.application.query.manageTradingCompany.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.services.IManageTradingCompanyService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageTradingCompanyQueryHandler implements IQueryHandler<GetSearchManageTradingCompanyQuery, PaginatedResponse> {

    private final IManageTradingCompanyService service;

    public GetSearchManageTradingCompanyQueryHandler(IManageTradingCompanyService service){
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageTradingCompanyQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
