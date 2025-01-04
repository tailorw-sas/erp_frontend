package com.kynsoft.finamer.creditcard.application.command.manageBankAccount.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageBankAccountMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "DELETE_MANAGE_BANK_ACCOUNT";

    public DeleteManageBankAccountMessage(UUID id) {
        this.id = id;
    }
}
