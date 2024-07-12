package com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class CreateCreditCardCloseOperationMessage implements ICommandMessage {

    private UUID id;

    public CreateCreditCardCloseOperationMessage(UUID id) {
        this.id = id;
    }

}
