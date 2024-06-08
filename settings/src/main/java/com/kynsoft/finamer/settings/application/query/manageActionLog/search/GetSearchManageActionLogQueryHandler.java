package com.kynsoft.finamer.settings.application.query.manageActionLog.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageActionLogService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageActionLogQueryHandler implements IQueryHandler<GetSearchManageActionLogQuery, PaginatedResponse> {

    private final IManageActionLogService service;

    public GetSearchManageActionLogQueryHandler(IManageActionLogService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageActionLogQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
