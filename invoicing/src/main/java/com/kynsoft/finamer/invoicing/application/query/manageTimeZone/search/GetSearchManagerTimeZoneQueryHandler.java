package com.kynsoft.finamer.invoicing.application.query.manageTimeZone.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManagerTimeZoneService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerTimeZoneQueryHandler implements IQueryHandler<GetSearchManagerTimeZoneQuery, PaginatedResponse> {

    private final IManagerTimeZoneService service;

    public GetSearchManagerTimeZoneQueryHandler(IManagerTimeZoneService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerTimeZoneQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
