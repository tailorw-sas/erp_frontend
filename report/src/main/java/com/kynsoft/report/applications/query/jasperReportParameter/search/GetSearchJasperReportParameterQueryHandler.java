package com.kynsoft.report.applications.query.jasperReportParameter.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.services.IReportParameterService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchJasperReportParameterQueryHandler implements IQueryHandler<GetSearchJasperReportParameterQuery, PaginatedResponse> {

    private final IReportParameterService service;

    public GetSearchJasperReportParameterQueryHandler(IReportParameterService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchJasperReportParameterQuery query) {
        return this.service.search(query.getPageable(),query.getFilter());
    }
}
