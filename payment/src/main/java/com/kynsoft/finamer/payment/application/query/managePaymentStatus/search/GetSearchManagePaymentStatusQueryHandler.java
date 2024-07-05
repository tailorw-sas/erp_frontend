package com.kynsoft.finamer.payment.application.query.managePaymentStatus.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagePaymentStatusQueryHandler implements IQueryHandler<GetSearchManagePaymentStatusQuery, PaginatedResponse> {
    private final IManagePaymentStatusService service;
    
    public GetSearchManagePaymentStatusQueryHandler(IManagePaymentStatusService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagePaymentStatusQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
