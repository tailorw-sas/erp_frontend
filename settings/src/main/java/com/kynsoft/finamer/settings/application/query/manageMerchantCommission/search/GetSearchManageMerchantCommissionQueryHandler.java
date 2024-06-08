package com.kynsoft.finamer.settings.application.query.manageMerchantCommission.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantCommissionService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageMerchantCommissionQueryHandler implements IQueryHandler<GetSearchManageMerchantCommissionQuery, PaginatedResponse> {

    private final IManageMerchantCommissionService service;

    public GetSearchManageMerchantCommissionQueryHandler(IManageMerchantCommissionService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageMerchantCommissionQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
