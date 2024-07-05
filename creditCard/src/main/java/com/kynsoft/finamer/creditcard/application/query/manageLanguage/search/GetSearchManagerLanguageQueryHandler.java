package com.kynsoft.finamer.creditcard.application.query.manageLanguage.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageLanguageService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerLanguageQueryHandler implements IQueryHandler<GetSearchManageLanguageQuery, PaginatedResponse> {

    private final IManageLanguageService service;

    public GetSearchManagerLanguageQueryHandler(IManageLanguageService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageLanguageQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
