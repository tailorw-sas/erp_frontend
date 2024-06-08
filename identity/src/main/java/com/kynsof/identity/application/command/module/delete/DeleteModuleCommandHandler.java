package com.kynsof.identity.application.command.module.delete;

import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.identity.domain.interfaces.service.IModuleService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class DeleteModuleCommandHandler implements ICommandHandler<DeleteModuleCommand> {

    private final IModuleService service;

    public DeleteModuleCommandHandler(IModuleService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteModuleCommand command) {
        ModuleDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
