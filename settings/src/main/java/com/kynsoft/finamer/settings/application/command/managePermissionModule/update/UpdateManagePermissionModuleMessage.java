package com.kynsoft.finamer.settings.application.command.managePermissionModule.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagePermissionModuleMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_PERMISSION_MODULE";

    public UpdateManagePermissionModuleMessage(UUID id) {
        this.id = id;
    }
}
