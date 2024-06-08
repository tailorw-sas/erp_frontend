package com.kynsoft.finamer.settings.application.query.manageDepartmentGroup.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageDepartmentGroupResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageDepartmentGroupDto;
import com.kynsoft.finamer.settings.domain.services.IManageDepartmentGroupService;
import org.springframework.stereotype.Component;

@Component
public class FindManageDepartmentGroupByIdQueryHandler implements IQueryHandler<FindManageDepartmentGroupByIdQuery, ManageDepartmentGroupResponse>  {

    private final IManageDepartmentGroupService service;

    public FindManageDepartmentGroupByIdQueryHandler(IManageDepartmentGroupService service) {
        this.service = service;
    }

    @Override
    public ManageDepartmentGroupResponse handle(FindManageDepartmentGroupByIdQuery query) {
        ManageDepartmentGroupDto response = service.findById(query.getId());

        return new ManageDepartmentGroupResponse(response);
    }
}
