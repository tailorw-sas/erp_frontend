package com.kynsoft.finamer.invoicing.application.query.manageLanguage.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageLanguageService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerLanguageQueryHandler implements IQueryHandler<GetSearchManagerLanguageQuery, PaginatedResponse> {

    private final IManageLanguageService service;

    public GetSearchManagerLanguageQueryHandler(IManageLanguageService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerLanguageQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
