package com.kynsoft.finamer.payment.application.query.payment.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchPaymentQueryHandler implements IQueryHandler<GetSearchPaymentQuery, PaginatedResponse> {
    private final IPaymentService service;
    
    public GetSearchPaymentQueryHandler(IPaymentService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchPaymentQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
