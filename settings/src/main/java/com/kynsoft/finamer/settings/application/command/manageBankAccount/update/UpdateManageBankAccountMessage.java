package com.kynsoft.finamer.settings.application.command.manageBankAccount.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageBankAccountMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_BANK_ACCOUNT";

    public UpdateManageBankAccountMessage(UUID id) {
        this.id = id;
    }
}
