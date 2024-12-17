package com.kynsoft.finamer.creditcard.application.query.manageMerchant.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageMerchantQueryHandler implements IQueryHandler<GetSearchManageMerchantQuery, PaginatedResponse>{
    private final IManageMerchantService service;

    public GetSearchManageMerchantQueryHandler(IManageMerchantService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageMerchantQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
