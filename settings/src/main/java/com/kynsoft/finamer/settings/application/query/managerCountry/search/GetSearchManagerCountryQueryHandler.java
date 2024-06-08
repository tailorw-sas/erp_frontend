package com.kynsoft.finamer.settings.application.query.managerCountry.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagerCountryService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerCountryQueryHandler implements IQueryHandler<GetSearchManagerCountryQuery, PaginatedResponse>{
    private final IManagerCountryService service;

    public GetSearchManagerCountryQueryHandler(IManagerCountryService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerCountryQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
