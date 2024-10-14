package com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.paginatedLanguages;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IMerchantLanguageCodeService;
import org.springframework.stereotype.Component;

@Component
public class GetPaginatedLanguagesQueryHandler implements IQueryHandler<GetPaginatedLanguagesQuery, PaginatedResponse>{
    private final IMerchantLanguageCodeService service;

    public GetPaginatedLanguagesQueryHandler(IMerchantLanguageCodeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetPaginatedLanguagesQuery query) {

        return this.service.findManageLanguages(query.getPageable(),query.getFilter());
    }
}
