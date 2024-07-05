package com.kynsoft.finamer.settings.application.command.manageModule.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageModuleDto;
import com.kynsoft.finamer.settings.domain.services.IManageModuleService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageModuleCommandHandler implements ICommandHandler<DeleteManageModuleCommand> {

    private final IManageModuleService service;

    public DeleteManageModuleCommandHandler(IManageModuleService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageModuleCommand command) {
        ManageModuleDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
