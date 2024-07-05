package com.kynsoft.finamer.creditcard.application.query.managerMerchant.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerMerchantQueryHandler implements IQueryHandler<GetSearchManagerMerchantQuery, PaginatedResponse>{
    private final IManageMerchantService service;

    public GetSearchManagerMerchantQueryHandler(IManageMerchantService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerMerchantQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
