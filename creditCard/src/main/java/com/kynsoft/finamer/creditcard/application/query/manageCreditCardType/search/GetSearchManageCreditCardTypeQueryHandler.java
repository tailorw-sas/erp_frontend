package com.kynsoft.finamer.creditcard.application.query.manageCreditCardType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageCreditCardTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageCreditCardTypeQueryHandler implements IQueryHandler<GetSearchManageCreditCardTypeQuery, PaginatedResponse>{
    private final IManageCreditCardTypeService service;

    public GetSearchManageCreditCardTypeQueryHandler(IManageCreditCardTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageCreditCardTypeQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
