package com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.updateAll;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class UpdateAllCreditCardCloseOperationMessage implements ICommandMessage {

    private final String command = "UPDATE_ALL";

    public UpdateAllCreditCardCloseOperationMessage() {
    }

}
