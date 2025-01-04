package com.kynsoft.finamer.payment.application.query.manageAgencyType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchAgencyTypeQueryHandler implements IQueryHandler<GetSearchAgencyTypeQuery, PaginatedResponse> {
    private final IManageAgencyTypeService service;

    public GetSearchAgencyTypeQueryHandler(IManageAgencyTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchAgencyTypeQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
