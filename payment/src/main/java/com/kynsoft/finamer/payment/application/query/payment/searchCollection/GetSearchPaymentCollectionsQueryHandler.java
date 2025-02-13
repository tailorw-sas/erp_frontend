package com.kynsoft.finamer.payment.application.query.payment.searchCollection;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchPaymentCollectionsQueryHandler implements IQueryHandler<GetSearchPaymentCollectionsQuery, PaginatedResponse> {
    private final IPaymentService service;
    
    public GetSearchPaymentCollectionsQueryHandler(IPaymentService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchPaymentCollectionsQuery query) {

        return this.service.searchCollections(query.getPageable(),query.getFilter(), query.getEmployeeId());
    }
}
