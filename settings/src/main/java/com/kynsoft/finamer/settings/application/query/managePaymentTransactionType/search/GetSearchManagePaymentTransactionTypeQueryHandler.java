package com.kynsoft.finamer.settings.application.query.managePaymentTransactionType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagePaymentTransactionTypeQueryHandler implements IQueryHandler<GetManagePaymentTransactionTypeQuery, PaginatedResponse>{
    private final IManagePaymentTransactionTypeService service;

    public GetSearchManagePaymentTransactionTypeQueryHandler(IManagePaymentTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetManagePaymentTransactionTypeQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
