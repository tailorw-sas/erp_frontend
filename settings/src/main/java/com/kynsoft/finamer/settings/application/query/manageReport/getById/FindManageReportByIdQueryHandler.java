package com.kynsoft.finamer.settings.application.query.manageReport.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageReportResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageReportDto;
import com.kynsoft.finamer.settings.domain.services.IManageReportService;
import org.springframework.stereotype.Component;

@Component
public class FindManageReportByIdQueryHandler implements IQueryHandler<FindManageReportByIdQuery, ManageReportResponse> {

    private final IManageReportService service;

    public FindManageReportByIdQueryHandler(IManageReportService service) {
        this.service = service;
    }

    @Override
    public ManageReportResponse handle(FindManageReportByIdQuery query) {
        ManageReportDto dto = service.findById(query.getId());

        return new ManageReportResponse(dto);
    }
}
