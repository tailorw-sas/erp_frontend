package com.kynsoft.finamer.settings.application.query.manageAgencyType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageAgencyTypeQueryHandler implements IQueryHandler<GetSearchManageAgencyTypeQuery, PaginatedResponse> {

    private final IManageAgencyTypeService service;

    public GetSearchManageAgencyTypeQueryHandler(IManageAgencyTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageAgencyTypeQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
