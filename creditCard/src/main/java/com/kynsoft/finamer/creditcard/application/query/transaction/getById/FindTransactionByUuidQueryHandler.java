package com.kynsoft.finamer.creditcard.application.query.transaction.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.TransactionResponse;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

@Component
public class FindTransactionByUuidQueryHandler implements IQueryHandler<FindTransactionByUuidQuery, TransactionResponse> {

    private final ITransactionService service;

    public FindTransactionByUuidQueryHandler(ITransactionService service) {
        this.service = service;
    }

    @Override
    public TransactionResponse handle(FindTransactionByUuidQuery query) {
        TransactionDto dto = this.service.findByUuid(query.getTransactionUuid());
        return new TransactionResponse(dto);
    }
}
