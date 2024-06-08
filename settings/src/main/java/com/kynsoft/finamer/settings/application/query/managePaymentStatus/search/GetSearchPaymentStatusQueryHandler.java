package com.kynsoft.finamer.settings.application.query.managePaymentStatus.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagerPaymentStatusService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchPaymentStatusQueryHandler implements IQueryHandler<GetSearchPaymentStatusQuery, PaginatedResponse> {
    private final IManagerPaymentStatusService service;
    
    public GetSearchPaymentStatusQueryHandler(final IManagerPaymentStatusService service) {
        this.service = service;
    }
    
    @Override
    public PaginatedResponse handle(GetSearchPaymentStatusQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
