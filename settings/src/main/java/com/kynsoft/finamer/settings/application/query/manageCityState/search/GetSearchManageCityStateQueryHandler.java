package com.kynsoft.finamer.settings.application.query.manageCityState.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageCityStateService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageCityStateQueryHandler implements IQueryHandler<GetSearchManageCityStateQuery, PaginatedResponse> {

    private final IManageCityStateService service;

    public GetSearchManageCityStateQueryHandler(final IManageCityStateService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageCityStateQuery query) {
        return this.service.search(query.getPageable(), query.getFilter());
    }
}
