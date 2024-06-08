package com.kynsof.identity.application.command.permission.update;

import com.kynsof.identity.domain.dto.enumType.PermissionStatusEnm;
import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdatePermissionCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private UUID idModule;
    private PermissionStatusEnm status;
    private String action;

    public UpdatePermissionCommand(UUID id, String code, String description, UUID idModule, PermissionStatusEnm status,
                                   String action) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.idModule = idModule;
        this.status = status;
        this.action = action;
    }

    public static UpdatePermissionCommand fromRequest(UpdatePermissionRequest request, UUID id) {
        return new UpdatePermissionCommand(id, request.getCode(), request.getDescription(),
                request.getModule(), request.getStatus(), request.getAction());
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdatePermissionMessage(id);
    }
}
