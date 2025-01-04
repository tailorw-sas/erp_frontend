package com.kynsoft.finamer.invoicing.application.query.manageAgencyContact.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyContactService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageAgencyContactQueryHandler implements IQueryHandler<GetSearchManageAgencyContactQuery, PaginatedResponse> {

    private final IManageAgencyContactService service;

    public GetSearchManageAgencyContactQueryHandler(IManageAgencyContactService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageAgencyContactQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
