package com.kynsoft.finamer.settings.application.command.managePermission.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagePermissionMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_MODULE";

    public CreateManagePermissionMessage(UUID id) {
        this.id = id;
    }

}
