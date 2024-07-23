package com.kynsoft.finamer.invoicing.application.query.income.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IIncomeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchIncomeQueryHandler implements IQueryHandler<GetSearchIncomeQuery, PaginatedResponse> {
    private final IIncomeService service;
    
    public GetSearchIncomeQueryHandler(IIncomeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchIncomeQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
