package com.kynsoft.finamer.settings.application.query.manageEmployeeGroup.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageEmployeeGroupResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeGroupDto;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeGroupService;
import org.springframework.stereotype.Component;

@Component
public class FindManageEmployeeGroupByIdQueryHandler implements IQueryHandler<FindManageEmployeeGroupByIdQuery, ManageEmployeeGroupResponse>  {

    private final IManageEmployeeGroupService service;

    public FindManageEmployeeGroupByIdQueryHandler(IManageEmployeeGroupService service) {
        this.service = service;
    }

    @Override
    public ManageEmployeeGroupResponse handle(FindManageEmployeeGroupByIdQuery query) {
        ManageEmployeeGroupDto response = service.findById(query.getId());

        return new ManageEmployeeGroupResponse(response);
    }
}
