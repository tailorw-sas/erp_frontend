package com.kynsoft.finamer.settings.application.query.managePaymentResource.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentSourceService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchPaymentSourceQueryHandler implements IQueryHandler<GetSearchPaymentSourceQuery, PaginatedResponse> {

    private final IManagePaymentSourceService service;

    public GetSearchPaymentSourceQueryHandler(IManagePaymentSourceService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchPaymentSourceQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
