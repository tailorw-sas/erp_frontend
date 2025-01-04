package com.kynsoft.finamer.creditcard.application.query.manageReconcileTransactionStatus.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageReconcileTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageReconcileTransactionStatusQueryHandler implements IQueryHandler<GetManageReconcileTransactionStatusQuery, PaginatedResponse>{
    private final IManageReconcileTransactionStatusService service;

    public GetSearchManageReconcileTransactionStatusQueryHandler(IManageReconcileTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetManageReconcileTransactionStatusQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
