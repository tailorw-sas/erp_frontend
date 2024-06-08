package com.kynsoft.report.applications.query.reportTemplate;

import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import com.kynsoft.report.domain.services.IReportService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

@Component
public class GetReportParameterByCodeQueryHandler implements IQueryHandler<GetReportParameterByCodeQuery, GetReportParameterByCodeResponse>  {

    private final IReportService reportService;
    private final IJasperReportTemplateService jasperReportTemplateService;

    public GetReportParameterByCodeQueryHandler(IReportService reportService, IJasperReportTemplateService jasperReportTemplateService) {

        this.reportService = reportService;
        this.jasperReportTemplateService = jasperReportTemplateService;
    }

    @Override
    public GetReportParameterByCodeResponse handle(GetReportParameterByCodeQuery query) {
        JasperReportTemplateDto jasperReportTemplateDto = jasperReportTemplateService.findByTemplateCode(query.getReportCode());
        String response = reportService.getReportParameters(jasperReportTemplateDto.getTemplateContentUrl());
        return  new GetReportParameterByCodeResponse(response);
    }
}
