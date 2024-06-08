package com.kynsoft.finamer.settings.application.query.manageB2BPartnerType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageB2BPartnerTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerB2BPartnerTypeQueryHandler implements IQueryHandler<GetSearchManagerB2BPartnerTypeQuery, PaginatedResponse>{
    private final IManageB2BPartnerTypeService service;

    public GetSearchManagerB2BPartnerTypeQueryHandler(IManageB2BPartnerTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerB2BPartnerTypeQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
