package com.kynsof.identity.application.command.user.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateUserSystemMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_USER";

    public CreateUserSystemMessage(UUID id) {
        this.id = id;
    }

}
