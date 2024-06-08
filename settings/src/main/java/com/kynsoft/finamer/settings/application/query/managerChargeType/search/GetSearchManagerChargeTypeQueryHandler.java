package com.kynsoft.finamer.settings.application.query.managerChargeType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagerChargeTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerChargeTypeQueryHandler implements IQueryHandler<GetSearchManagerChargeTypeQuery, PaginatedResponse> {

    private final IManagerChargeTypeService service;

    public GetSearchManagerChargeTypeQueryHandler(IManagerChargeTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerChargeTypeQuery query) {

        return this.service.search(query.getPageable(), query.getFilter());
    }
}
