package com.kynsoft.finamer.invoicing.application.command.manageCurrency.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageCurrencyMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGER_CURRENCY";

    public CreateManageCurrencyMessage(UUID id) {
        this.id = id;
    }

}
