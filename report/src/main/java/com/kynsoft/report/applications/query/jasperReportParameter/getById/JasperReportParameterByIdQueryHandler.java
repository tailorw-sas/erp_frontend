package com.kynsoft.report.applications.query.jasperReportParameter.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.report.domain.dto.JasperReportParameterDto;
import com.kynsoft.report.domain.services.IReportParameterService;
import org.springframework.stereotype.Component;

@Component
public class JasperReportParameterByIdQueryHandler implements IQueryHandler<JasperReportParameterByIdQuery, JasperReportParameterResponse> {

    private final IReportParameterService service;

    public JasperReportParameterByIdQueryHandler(IReportParameterService service) {
        this.service = service;
    }

    @Override
    public JasperReportParameterResponse handle(JasperReportParameterByIdQuery query) {
        JasperReportParameterDto dto = this.service.findById(query.getId());
        return new JasperReportParameterResponse(dto);
    }
}
