package com.kynsof.identity.application.command.module.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteModuleMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MODULE";

    public DeleteModuleMessage(UUID id) {
        this.id = id;
    }

}
