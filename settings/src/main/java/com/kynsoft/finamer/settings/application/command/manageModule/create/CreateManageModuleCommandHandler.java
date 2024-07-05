package com.kynsoft.finamer.settings.application.command.manageModule.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageModuleDto;
import com.kynsoft.finamer.settings.domain.services.IManageModuleService;

import org.springframework.stereotype.Component;

@Component
public class CreateManageModuleCommandHandler implements ICommandHandler<CreateManageModuleCommand> {

    private final IManageModuleService service;


    public CreateManageModuleCommandHandler(IManageModuleService service) {

        this.service = service;
    }

    @Override
    public void handle(CreateManageModuleCommand command) {
        service.create(new ManageModuleDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus()
        ));
    }
}
