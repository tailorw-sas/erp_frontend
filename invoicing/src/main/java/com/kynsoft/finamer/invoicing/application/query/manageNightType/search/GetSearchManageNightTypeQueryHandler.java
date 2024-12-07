package com.kynsoft.finamer.invoicing.application.query.manageNightType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageLanguageService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageNightTypeQueryHandler implements IQueryHandler<GetSearchManageNightTypeQuery, PaginatedResponse> {

    private final IManageLanguageService service;

    public GetSearchManageNightTypeQueryHandler(IManageLanguageService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageNightTypeQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
