package com.kynsoft.finamer.settings.application.command.managePermissionModule.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManagePermissionModuleMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_PERMISSION_MODULE";

    public DeleteManagePermissionModuleMessage(UUID id) {
        this.id = id;
    }
}
