package com.kynsoft.finamer.settings.application.command.manageCreditCardType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagerCreditCardTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_CREDIT_CARD_TYPE";

    public CreateManagerCreditCardTypeMessage(UUID id) {
        this.id = id;
    }

}
