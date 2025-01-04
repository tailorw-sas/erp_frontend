package com.kynsoft.finamer.creditcard.application.query.TransactionStatusHistory.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionStatusHistoryService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchTransactionStatusHistoryQueryHandler implements IQueryHandler<GetSearchTransactionStatusHistoryQuery, PaginatedResponse> {

    private final ITransactionStatusHistoryService service;

    public GetSearchTransactionStatusHistoryQueryHandler(ITransactionStatusHistoryService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchTransactionStatusHistoryQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
