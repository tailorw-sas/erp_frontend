package com.kynsof.identity.application.command.permission.delete;

import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.dto.enumType.PermissionStatusEnm;
import com.kynsof.identity.domain.interfaces.service.IPermissionService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeletePermissionCommandHandler implements ICommandHandler<DeletePermissionCommand> {

    private final IPermissionService service;

    public DeletePermissionCommandHandler(IPermissionService service) {
        this.service = service;
    }

    @Override
    public void handle(DeletePermissionCommand command) {
        PermissionDto delete = this.service.findById(command.getId());

        delete.setStatus(PermissionStatusEnm.INACTIVE);
        delete.setCode(delete.getCode() + "-" + UUID.randomUUID());
        delete.setDeleted(true);

        service.delete(delete);
    }

}
