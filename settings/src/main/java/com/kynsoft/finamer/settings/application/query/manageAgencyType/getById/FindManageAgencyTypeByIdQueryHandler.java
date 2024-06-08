package com.kynsoft.finamer.settings.application.query.manageAgencyType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageAgencyTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManageAgencyTypeByIdQueryHandler implements IQueryHandler<FindManageAgencyTypeByIdQuery, ManageAgencyTypeResponse> {

    private final IManageAgencyTypeService service;

    public FindManageAgencyTypeByIdQueryHandler(IManageAgencyTypeService service) {
        this.service = service;
    }

    @Override
    public ManageAgencyTypeResponse handle(FindManageAgencyTypeByIdQuery query) {
        ManageAgencyTypeDto dto = service.findById(query.getId());
        return new ManageAgencyTypeResponse(dto);
    }
}
