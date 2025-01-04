package com.kynsoft.finamer.creditcard.application.query.manageMerchantConfig.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantConfigService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageMerchantConfigQueryHandler implements IQueryHandler<GetSearchManageMerchantConfigQuery, PaginatedResponse> {

    private final IManageMerchantConfigService service;

    public GetSearchManageMerchantConfigQueryHandler(IManageMerchantConfigService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageMerchantConfigQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
