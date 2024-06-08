package com.kynsoft.finamer.settings.application.command.manageActionLog.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageActionLogMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_ACTION_LOG";

    public DeleteManageActionLogMessage(UUID id) {
        this.id = id;
    }
}
