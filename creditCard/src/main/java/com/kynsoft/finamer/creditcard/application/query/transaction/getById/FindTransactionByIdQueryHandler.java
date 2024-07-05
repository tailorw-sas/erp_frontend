package com.kynsoft.finamer.creditcard.application.query.transaction.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.TransactionResponse;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

@Component
public class FindTransactionByIdQueryHandler implements IQueryHandler<FindTransactionByIdQuery, TransactionResponse> {

    private final ITransactionService service;

    public FindTransactionByIdQueryHandler(ITransactionService service) {
        this.service = service;
    }

    @Override
    public TransactionResponse handle(FindTransactionByIdQuery query) {
        TransactionDto dto = this.service.findById(query.getId());
        return new TransactionResponse(dto);
    }
}
