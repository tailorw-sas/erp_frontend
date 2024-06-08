package com.kynsoft.finamer.settings.application.command.manageCreditCardType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageCreditCardTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_CREDIT_CART_TYPE";

    public DeleteManageCreditCardTypeMessage(UUID id) {
        this.id = id;
    }

}
