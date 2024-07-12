package com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteCreditCardCloseOperationMessage implements ICommandMessage {

    private final UUID id;

    public DeleteCreditCardCloseOperationMessage(UUID id) {
        this.id = id;
    }

}
