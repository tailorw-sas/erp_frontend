package com.kynsoft.finamer.payment.application.query.managePaymentSource.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentSourceService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagePaymentSourceQueryHandler implements IQueryHandler<GetSearchManagePaymentSourceQuery, PaginatedResponse> {
    private final IManagePaymentSourceService service;
    
    public GetSearchManagePaymentSourceQueryHandler(IManagePaymentSourceService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagePaymentSourceQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
