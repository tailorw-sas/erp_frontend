package com.kynsoft.finamer.invoicing.application.query.manageCurrency.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageCurrencyService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerCurrencyQueryHandler implements IQueryHandler<GetSearchManagerCurrencyQuery, PaginatedResponse>{
    private final IManageCurrencyService service;

    public GetSearchManagerCurrencyQueryHandler(IManageCurrencyService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerCurrencyQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
