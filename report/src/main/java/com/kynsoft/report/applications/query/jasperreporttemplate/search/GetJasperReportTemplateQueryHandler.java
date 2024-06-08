package com.kynsoft.report.applications.query.jasperreporttemplate.search;

import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.stereotype.Component;

@Component
public class GetJasperReportTemplateQueryHandler implements IQueryHandler<GetJasperReportTemplateQuery, PaginatedResponse>{

    private final IJasperReportTemplateService serviceImpl;

    public GetJasperReportTemplateQueryHandler(IJasperReportTemplateService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public PaginatedResponse handle(GetJasperReportTemplateQuery query) {

        return this.serviceImpl.search(query.getPageable(),query.getFilter());
    }
}
