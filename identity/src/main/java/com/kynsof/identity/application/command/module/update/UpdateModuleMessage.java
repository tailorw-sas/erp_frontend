package com.kynsof.identity.application.command.module.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateModuleMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MODULE";

    public UpdateModuleMessage(UUID id) {
        this.id = id;
    }

}
