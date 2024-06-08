package com.kynsoft.finamer.settings.application.command.manageClient.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageClientMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_CLIENT";

    public DeleteManageClientMessage(UUID id) {
        this.id = id;
    }

}
