package com.kynsoft.finamer.settings.application.query.manageReportParamType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageReportParamTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageReportParamTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageReportParamTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManageReportParamTypeByIdQueryHandler implements IQueryHandler<FindManageReportParamTypeByIdQuery, ManageReportParamTypeResponse> {

    private final IManageReportParamTypeService service;

    public FindManageReportParamTypeByIdQueryHandler(IManageReportParamTypeService service) {
        this.service = service;
    }

    @Override
    public ManageReportParamTypeResponse handle(FindManageReportParamTypeByIdQuery query) {
        ManageReportParamTypeDto dto = service.findById(query.getId());
        return new ManageReportParamTypeResponse(dto);
    }
}
