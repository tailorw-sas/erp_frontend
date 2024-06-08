package com.kynsoft.finamer.settings.application.command.managePermissionModule.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagePermissionModuleDto;
import com.kynsoft.finamer.settings.domain.services.IManagePermissionModuleService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagePermissionModuleCommandHandler implements ICommandHandler<DeleteManagePermissionModuleCommand> {

    private final IManagePermissionModuleService service;

    public DeleteManagePermissionModuleCommandHandler(IManagePermissionModuleService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagePermissionModuleCommand command) {
        ManagePermissionModuleDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
