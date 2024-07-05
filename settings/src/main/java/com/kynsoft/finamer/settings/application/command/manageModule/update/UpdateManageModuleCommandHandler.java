package com.kynsoft.finamer.settings.application.command.manageModule.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageModuleDto;
import com.kynsoft.finamer.settings.domain.services.IManageModuleService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageModuleCommandHandler implements ICommandHandler<UpdateManageModuleCommand> {

    private final IManageModuleService service;

    public UpdateManageModuleCommandHandler(IManageModuleService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageModuleCommand command) {
        ManageModuleDto test = this.service.findById(command.getId());
        this.service.update(test);
    }
}
