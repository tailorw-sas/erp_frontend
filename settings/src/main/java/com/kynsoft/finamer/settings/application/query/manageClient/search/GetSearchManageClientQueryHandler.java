package com.kynsoft.finamer.settings.application.query.manageClient.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManagerClientService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageClientQueryHandler implements IQueryHandler<GetSearchManageClientQuery, PaginatedResponse> {

    private final IManagerClientService service;

    public GetSearchManageClientQueryHandler(final IManagerClientService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageClientQuery query) {
        return this.service.search(query.getPageable(), query.getFilter());
    }
}
