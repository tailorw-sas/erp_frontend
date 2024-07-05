package com.kynsoft.finamer.settings.application.query.manageReport.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.services.IManageReportService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageReportQueryHandler implements IQueryHandler<GetSearchManageReportQuery, PaginatedResponse> {

    private final IManageReportService service;

    public GetSearchManageReportQueryHandler(IManageReportService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageReportQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
