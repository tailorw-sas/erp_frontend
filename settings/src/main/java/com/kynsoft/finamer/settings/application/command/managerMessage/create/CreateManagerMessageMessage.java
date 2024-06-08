package com.kynsoft.finamer.settings.application.command.managerMessage.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagerMessageMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGER_MESSAGE";

    public CreateManagerMessageMessage(UUID id) {
        this.id = id;
    }
}
