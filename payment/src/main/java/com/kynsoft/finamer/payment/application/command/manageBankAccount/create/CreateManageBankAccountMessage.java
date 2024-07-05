package com.kynsoft.finamer.payment.application.command.manageBankAccount.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageBankAccountMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_BANK_ACCOUNT";

    public CreateManageBankAccountMessage(UUID id) {
        this.id = id;
    }
}
