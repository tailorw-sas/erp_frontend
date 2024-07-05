package com.kynsoft.finamer.payment.application.command.payment.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreatePaymentMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_PAYMENT";

    public CreatePaymentMessage(UUID id) {
        this.id = id;
    }

}
