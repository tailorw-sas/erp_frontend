package com.kynsoft.finamer.settings.application.command.manageBank.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagerBankMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGER_BANK";

    public UpdateManagerBankMessage(UUID id) {
        this.id = id;
    }

}
