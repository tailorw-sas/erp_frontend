package com.kynsoft.finamer.payment.application.query.paymentcloseoperation.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchPaymentCloseOperationQueryHandler implements IQueryHandler<GetSearchPaymentCloseOperationQuery, PaginatedResponse> {
    private final IPaymentCloseOperationService service;
    
    public GetSearchPaymentCloseOperationQueryHandler(IPaymentCloseOperationService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchPaymentCloseOperationQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
