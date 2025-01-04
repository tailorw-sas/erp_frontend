package com.kynsoft.finamer.creditcard.application.command.manageCurrency.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManagerCurrencyMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGER_CURRENCY";

    public DeleteManagerCurrencyMessage(UUID id) {
        this.id = id;
    }

}
