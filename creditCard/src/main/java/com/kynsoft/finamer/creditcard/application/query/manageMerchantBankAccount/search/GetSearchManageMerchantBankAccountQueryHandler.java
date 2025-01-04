package com.kynsoft.finamer.creditcard.application.query.manageMerchantBankAccount.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantBankAccountService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageMerchantBankAccountQueryHandler implements IQueryHandler<GetSearchManageMerchantBankAccountQuery, PaginatedResponse> {

    private final IManageMerchantBankAccountService service;

    public GetSearchManageMerchantBankAccountQueryHandler(IManageMerchantBankAccountService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageMerchantBankAccountQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
