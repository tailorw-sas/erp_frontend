package com.kynsoft.finamer.creditcard.application.command.manageCreditCardType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageCreditCardTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_CREDIT_CARD_TYPE";

    public UpdateManageCreditCardTypeMessage(UUID id) {
        this.id = id;
    }

}
