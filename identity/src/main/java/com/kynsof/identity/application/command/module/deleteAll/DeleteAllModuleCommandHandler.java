package com.kynsof.identity.application.command.module.deleteAll;

import com.kynsof.identity.domain.interfaces.service.IModuleService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class DeleteAllModuleCommandHandler implements ICommandHandler<DeleteAllModuleCommand> {

    private final IModuleService serviceImpl;

    public DeleteAllModuleCommandHandler(IModuleService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteAllModuleCommand command) {

        serviceImpl.deleteAll(command.getIds());
    }

}
