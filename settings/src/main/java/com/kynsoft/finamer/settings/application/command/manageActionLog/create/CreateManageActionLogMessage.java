package com.kynsoft.finamer.settings.application.command.manageActionLog.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageActionLogMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_ACTION_LOG";

    public CreateManageActionLogMessage(UUID id) {
        this.id = id;
    }
}
