package com.kynsoft.finamer.settings.application.command.manageModule.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageModuleMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_MODULE";

    public DeleteManageModuleMessage(UUID id) {
        this.id = id;
    }
}
