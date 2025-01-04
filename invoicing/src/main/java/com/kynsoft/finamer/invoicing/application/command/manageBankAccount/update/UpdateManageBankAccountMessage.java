package com.kynsoft.finamer.invoicing.application.command.manageBankAccount.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageBankAccountMessage implements ICommandMessage {

    private final UUID id;

    public UpdateManageBankAccountMessage(UUID id) {
        this.id = id;
    }

}
