package com.kynsoft.finamer.creditcard.application.query.creditCardCloseOperation.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.CreditCardCloseOperationResponse;
import com.kynsoft.finamer.creditcard.domain.dto.CreditCardCloseOperationDto;
import com.kynsoft.finamer.creditcard.domain.services.ICreditCardCloseOperationService;
import org.springframework.stereotype.Component;

@Component
public class FindCreditCardCloseOperationByIdQueryHandler implements IQueryHandler<FindCreditCardCloseOperationByIdQuery, CreditCardCloseOperationResponse>  {

    private final ICreditCardCloseOperationService closeOperationService;

    public FindCreditCardCloseOperationByIdQueryHandler(ICreditCardCloseOperationService closeOperationService) {
        this.closeOperationService = closeOperationService;
    }

    @Override
    public CreditCardCloseOperationResponse handle(FindCreditCardCloseOperationByIdQuery query) {
        CreditCardCloseOperationDto response = closeOperationService.findById(query.getId());

        return new CreditCardCloseOperationResponse(response);
    }
}
