package com.kynsoft.finamer.invoicing.application.query.managePaymentTransactionType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManagePaymentTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagePaymentTransactionTypeQueryHandler implements IQueryHandler<GetSearchManagePaymentTransactionTypeQuery, PaginatedResponse> {
    private final IManagePaymentTransactionTypeService service;
    
    public GetSearchManagePaymentTransactionTypeQueryHandler(IManagePaymentTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagePaymentTransactionTypeQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
