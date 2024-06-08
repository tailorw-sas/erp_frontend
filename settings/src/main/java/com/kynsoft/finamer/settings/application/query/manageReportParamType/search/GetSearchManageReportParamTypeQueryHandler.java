package com.kynsoft.finamer.settings.application.query.manageReportParamType.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageReportParamTypeService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageReportParamTypeQueryHandler implements IQueryHandler<GetSearchManageReportParamTypeQuery, PaginatedResponse> {

    private final IManageReportParamTypeService service;

    public GetSearchManageReportParamTypeQueryHandler(IManageReportParamTypeService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageReportParamTypeQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
