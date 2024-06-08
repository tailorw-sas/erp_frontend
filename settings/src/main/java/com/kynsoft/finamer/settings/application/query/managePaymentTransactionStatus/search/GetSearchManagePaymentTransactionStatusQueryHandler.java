package com.kynsoft.finamer.settings.application.query.managePaymentTransactionStatus.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagePaymentTransactionStatusQueryHandler implements IQueryHandler<GetManagePaymentTransactionStatusQuery, PaginatedResponse>{
    private final IManagePaymentTransactionStatusService service;

    public GetSearchManagePaymentTransactionStatusQueryHandler(IManagePaymentTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetManagePaymentTransactionStatusQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
