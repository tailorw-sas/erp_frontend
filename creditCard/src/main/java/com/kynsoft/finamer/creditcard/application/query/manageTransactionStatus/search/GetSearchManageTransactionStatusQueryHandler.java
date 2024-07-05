package com.kynsoft.finamer.creditcard.application.query.manageTransactionStatus.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageTransactionStatusQueryHandler implements IQueryHandler<GetSearchManageTransactionStatusQuery, PaginatedResponse>{
    private final IManageTransactionStatusService service;

    public GetSearchManageTransactionStatusQueryHandler(IManageTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageTransactionStatusQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
