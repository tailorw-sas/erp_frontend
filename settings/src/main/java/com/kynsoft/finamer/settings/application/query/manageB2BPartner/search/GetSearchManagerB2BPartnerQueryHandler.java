package com.kynsoft.finamer.settings.application.query.manageB2BPartner.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagerB2BPartnerService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManagerB2BPartnerQueryHandler implements IQueryHandler<GetSearchManagerB2BPartnerQuery, PaginatedResponse>{
    private final IManagerB2BPartnerService service;

    public GetSearchManagerB2BPartnerQueryHandler(IManagerB2BPartnerService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManagerB2BPartnerQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
