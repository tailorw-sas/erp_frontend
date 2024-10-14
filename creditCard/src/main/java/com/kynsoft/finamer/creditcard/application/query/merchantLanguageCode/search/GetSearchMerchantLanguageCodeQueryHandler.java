package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IMerchantLanguageCodeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchMerchantLanguageCodeQueryHandler implements IQueryHandler<GetSearchMerchantLanguageCodeQuery, PaginatedResponse>{
    private final IMerchantLanguageCodeService service;

    public GetSearchMerchantLanguageCodeQueryHandler(IMerchantLanguageCodeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchMerchantLanguageCodeQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
