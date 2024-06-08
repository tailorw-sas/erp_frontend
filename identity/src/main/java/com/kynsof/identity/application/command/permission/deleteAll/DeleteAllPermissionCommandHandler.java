package com.kynsof.identity.application.command.permission.deleteAll;

import com.kynsof.identity.domain.interfaces.service.IPermissionService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class DeleteAllPermissionCommandHandler implements ICommandHandler<DeleteAllPermissionCommand> {

    private final IPermissionService serviceImpl;

    public DeleteAllPermissionCommandHandler(IPermissionService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteAllPermissionCommand command) {

        serviceImpl.deleteAll(command.getIds());
    }

}
