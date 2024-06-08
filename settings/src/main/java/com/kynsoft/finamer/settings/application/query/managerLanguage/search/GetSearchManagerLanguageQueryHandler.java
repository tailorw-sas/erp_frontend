package com.kynsoft.finamer.settings.application.query.managerLanguage.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagerLanguageService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerLanguageQueryHandler implements IQueryHandler<GetSearchManagerLanguageQuery, PaginatedResponse> {

    private final IManagerLanguageService service;

    public GetSearchManagerLanguageQueryHandler(IManagerLanguageService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerLanguageQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
