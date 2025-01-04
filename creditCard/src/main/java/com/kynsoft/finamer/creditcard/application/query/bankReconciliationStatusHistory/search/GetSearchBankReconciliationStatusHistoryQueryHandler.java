package com.kynsoft.finamer.creditcard.application.query.bankReconciliationStatusHistory.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IBankReconciliationStatusHistoryService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionStatusHistoryService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchBankReconciliationStatusHistoryQueryHandler implements IQueryHandler<GetSearchBankReconciliationStatusHistoryQuery, PaginatedResponse> {

    private final IBankReconciliationStatusHistoryService service;

    public GetSearchBankReconciliationStatusHistoryQueryHandler(IBankReconciliationStatusHistoryService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchBankReconciliationStatusHistoryQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
