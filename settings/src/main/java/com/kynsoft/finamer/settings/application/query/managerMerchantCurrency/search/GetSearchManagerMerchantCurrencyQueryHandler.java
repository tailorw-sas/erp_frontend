package com.kynsoft.finamer.settings.application.query.managerMerchantCurrency.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantCurrencyService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerMerchantCurrencyQueryHandler implements IQueryHandler<GetSearchManagerMerchantCurrencyQuery, PaginatedResponse>{
    private final IManagerMerchantCurrencyService service;

    public GetSearchManagerMerchantCurrencyQueryHandler(IManagerMerchantCurrencyService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerMerchantCurrencyQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
