package com.kynsoft.finamer.settings.application.command.manageBank.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManagerBankMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGER_BANK";

    public DeleteManagerBankMessage(UUID id) {
        this.id = id;
    }

}
