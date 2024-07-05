package com.kynsoft.finamer.payment.application.command.payment.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdatePaymentMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_PAYMENT";

    public UpdatePaymentMessage(UUID id) {
        this.id = id;
    }

}
