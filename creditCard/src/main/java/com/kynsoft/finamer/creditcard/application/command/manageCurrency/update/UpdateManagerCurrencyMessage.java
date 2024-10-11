package com.kynsoft.finamer.creditcard.application.command.manageCurrency.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagerCurrencyMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGER_CURRENCY";

    public UpdateManagerCurrencyMessage(UUID id) {
        this.id = id;
    }

}
