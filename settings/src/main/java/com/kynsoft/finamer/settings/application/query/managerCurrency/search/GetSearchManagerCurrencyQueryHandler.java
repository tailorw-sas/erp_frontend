package com.kynsoft.finamer.settings.application.query.managerCurrency.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagerCurrencyService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerCurrencyQueryHandler implements IQueryHandler<GetSearchManagerCurrencyQuery, PaginatedResponse>{
    private final IManagerCurrencyService service;

    public GetSearchManagerCurrencyQueryHandler(IManagerCurrencyService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerCurrencyQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
