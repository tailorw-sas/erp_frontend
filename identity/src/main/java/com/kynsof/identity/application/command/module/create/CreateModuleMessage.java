package com.kynsof.identity.application.command.module.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateModuleMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MODULE";

    public CreateModuleMessage(UUID id) {
        this.id = id;
    }

}
