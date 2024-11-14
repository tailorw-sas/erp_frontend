package com.kynsoft.finamer.creditcard.application.query.transaction.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchTransactionQueryHandler implements IQueryHandler<GetSearchTransactionQuery, TransactionResumeResponse> {

    private final ITransactionService service;

    public GetSearchTransactionQueryHandler(ITransactionService service) {
        this.service = service;
    }

    @Override
    public TransactionResumeResponse handle(GetSearchTransactionQuery query) {
        TransactionTotalResume transactionTotalResume = service.searchTotal(query.getFilter());
        PaginatedResponse paginatedResponse = service.search(query.getPageable(), query.getFilter());

        return new TransactionResumeResponse(paginatedResponse, transactionTotalResume);
    }
}
