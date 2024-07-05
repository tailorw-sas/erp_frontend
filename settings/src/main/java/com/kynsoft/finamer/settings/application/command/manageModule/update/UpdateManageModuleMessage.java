package com.kynsoft.finamer.settings.application.command.manageModule.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageModuleMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_MODULE";

    public UpdateManageModuleMessage(UUID id) {
        this.id = id;
    }

}
