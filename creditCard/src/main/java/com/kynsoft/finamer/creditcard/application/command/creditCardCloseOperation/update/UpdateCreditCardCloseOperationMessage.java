package com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateCreditCardCloseOperationMessage implements ICommandMessage {

    private final UUID id;

    public UpdateCreditCardCloseOperationMessage(UUID id) {
        this.id = id;
    }

}
