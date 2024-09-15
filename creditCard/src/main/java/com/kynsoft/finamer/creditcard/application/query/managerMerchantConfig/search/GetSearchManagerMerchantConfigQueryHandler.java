package com.kynsoft.finamer.creditcard.application.query.managerMerchantConfig.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantConfigService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerMerchantConfigQueryHandler implements IQueryHandler<GetSearchManagerMerchantConfigQuery, PaginatedResponse> {

    private final IManageMerchantConfigService service;

    public GetSearchManagerMerchantConfigQueryHandler(IManageMerchantConfigService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(com.kynsoft.finamer.creditcard.application.query.managerMerchantConfig.search.GetSearchManagerMerchantConfigQuery query) {

        return this.service.search(query.getPageable(), query.getFilter());
    }
}
