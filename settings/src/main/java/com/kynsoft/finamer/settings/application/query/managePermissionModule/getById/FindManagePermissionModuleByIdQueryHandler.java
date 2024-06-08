package com.kynsoft.finamer.settings.application.query.managePermissionModule.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePermissionModuleResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePermissionModuleDto;
import com.kynsoft.finamer.settings.domain.services.IManagePermissionModuleService;
import org.springframework.stereotype.Component;

@Component
public class FindManagePermissionModuleByIdQueryHandler implements IQueryHandler<FindManagePermissionModuleByIdQuery, ManagePermissionModuleResponse>  {

    private final IManagePermissionModuleService service;

    public FindManagePermissionModuleByIdQueryHandler(IManagePermissionModuleService service) {
        this.service = service;
    }

    @Override
    public ManagePermissionModuleResponse handle(FindManagePermissionModuleByIdQuery query) {
        ManagePermissionModuleDto response = service.findById(query.getId());

        return new ManagePermissionModuleResponse(response);
    }
}
