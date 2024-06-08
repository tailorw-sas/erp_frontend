package com.kynsof.identity.application.command.permission.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdatePermissionMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_PERMISSION";

    public UpdatePermissionMessage(UUID id) {
        this.id = id;
    }

}
