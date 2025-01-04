package com.kynsoft.finamer.creditcard.application.query.managerBank.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManagerBankService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerBankQueryHandler implements IQueryHandler<GetSearchManagerBankQuery, PaginatedResponse>{
    private final IManagerBankService service;

    public GetSearchManagerBankQueryHandler(IManagerBankService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerBankQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
