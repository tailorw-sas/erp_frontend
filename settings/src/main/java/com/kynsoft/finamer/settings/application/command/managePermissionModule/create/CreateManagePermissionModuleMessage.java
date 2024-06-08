package com.kynsoft.finamer.settings.application.command.managePermissionModule.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagePermissionModuleMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_PERMISSION_MODULE";

    public CreateManagePermissionModuleMessage(UUID id) {
        this.id = id;
    }
}
