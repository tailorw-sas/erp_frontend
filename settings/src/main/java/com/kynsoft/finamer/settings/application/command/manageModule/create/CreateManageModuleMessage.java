package com.kynsoft.finamer.settings.application.command.manageModule.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageModuleMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_MODULE";

    public CreateManageModuleMessage(UUID id) {
        this.id = id;
    }

}
