package com.kynsoft.finamer.settings.application.command.manageActionLog.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageActionLogMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_ACTION_LOG";

    public UpdateManageActionLogMessage(UUID id) {
        this.id = id;
    }
}
