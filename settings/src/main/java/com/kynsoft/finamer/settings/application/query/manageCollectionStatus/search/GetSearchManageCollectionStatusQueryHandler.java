package com.kynsoft.finamer.settings.application.query.manageCollectionStatus.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageCollectionStatusService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageCollectionStatusQueryHandler implements IQueryHandler<GetSearchManageCollectionStatusQuery, PaginatedResponse> {

    private final IManageCollectionStatusService service;

    public GetSearchManageCollectionStatusQueryHandler(IManageCollectionStatusService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageCollectionStatusQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
