package com.kynsoft.finamer.settings.application.query.managerMerchant.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerMerchantQueryHandler implements IQueryHandler<GetSearchManagerMerchantQuery, PaginatedResponse>{
    private final IManagerMerchantService service;

    public GetSearchManagerMerchantQueryHandler(IManagerMerchantService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerMerchantQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
