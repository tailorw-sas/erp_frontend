package com.kynsof.identity.application.command.permission.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreatePermissionMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_PERMISSION";

    public CreatePermissionMessage(UUID id) {
        this.id = id;
    }

}
