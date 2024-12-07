package com.kynsoft.finamer.invoicing.application.query.manageTradingCompanies.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageTradingCompaniesService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageTradingCompaniesQueryHandler implements IQueryHandler<GetSearchManageTradingCompaniesQuery, PaginatedResponse> {

    private final IManageTradingCompaniesService service;

    public GetSearchManageTradingCompaniesQueryHandler(IManageTradingCompaniesService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageTradingCompaniesQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
