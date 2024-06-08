package com.kynsoft.finamer.settings.application.command.managerMessage.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagerMessageMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGER_MESSAGE";

    public UpdateManagerMessageMessage(UUID id) {
        this.id = id;
    }
}
