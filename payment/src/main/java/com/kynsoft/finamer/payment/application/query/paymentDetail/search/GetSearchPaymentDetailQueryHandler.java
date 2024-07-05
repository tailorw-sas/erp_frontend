package com.kynsoft.finamer.payment.application.query.paymentDetail.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchPaymentDetailQueryHandler implements IQueryHandler<GetSearchPaymentDetailQuery, PaginatedResponse> {
    private final IPaymentDetailService service;
    
    public GetSearchPaymentDetailQueryHandler(IPaymentDetailService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchPaymentDetailQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
