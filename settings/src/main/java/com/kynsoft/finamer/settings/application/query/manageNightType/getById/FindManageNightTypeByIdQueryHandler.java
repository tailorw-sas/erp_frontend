package com.kynsoft.finamer.settings.application.query.manageNightType.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageNightTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageNightTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageNightTypeService;
import org.springframework.stereotype.Component;

@Component
public class FindManageNightTypeByIdQueryHandler implements IQueryHandler<FindManageNightTypeByIdQuery, ManageNightTypeResponse> {

    private final IManageNightTypeService service;

    public FindManageNightTypeByIdQueryHandler(IManageNightTypeService service) {
        this.service = service;
    }

    @Override
    public ManageNightTypeResponse handle(FindManageNightTypeByIdQuery query) {
        ManageNightTypeDto dto = service.findById(query.getId());

        return new ManageNightTypeResponse(dto);
    }
}
