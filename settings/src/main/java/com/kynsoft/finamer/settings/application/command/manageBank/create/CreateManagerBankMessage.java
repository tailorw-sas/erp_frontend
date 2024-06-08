package com.kynsoft.finamer.settings.application.command.manageBank.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagerBankMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGER_BANK";

    public CreateManagerBankMessage(UUID id) {
        this.id = id;
    }

}
