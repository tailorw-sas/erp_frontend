package com.kynsoft.finamer.settings.application.command.managerMessage.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManagerMessageMessage implements ICommandMessage {

    private UUID id;

    private final String command = "DELETE_MANAGER_MESSAGE";

    public DeleteManagerMessageMessage(UUID id) {
        this.id = id;
    }
}
