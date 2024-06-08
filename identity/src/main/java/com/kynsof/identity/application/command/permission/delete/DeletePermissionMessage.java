package com.kynsof.identity.application.command.permission.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeletePermissionMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_PERMISSION";

    public DeletePermissionMessage(UUID id) {
        this.id = id;
    }

}
