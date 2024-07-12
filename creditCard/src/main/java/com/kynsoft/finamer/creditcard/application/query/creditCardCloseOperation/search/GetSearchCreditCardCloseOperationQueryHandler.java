package com.kynsoft.finamer.creditcard.application.query.creditCardCloseOperation.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.ICreditCardCloseOperationService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchCreditCardCloseOperationQueryHandler implements IQueryHandler<GetSearchCreditCardCloseOperationQuery, PaginatedResponse> {
    private final ICreditCardCloseOperationService closeOperationService;
    
    public GetSearchCreditCardCloseOperationQueryHandler(ICreditCardCloseOperationService closeOperationService) {
        this.closeOperationService = closeOperationService;
    }

    @Override
    public PaginatedResponse handle(GetSearchCreditCardCloseOperationQuery query) {

        return this.closeOperationService.search(query.getPageable(),query.getFilter());
    }
}
