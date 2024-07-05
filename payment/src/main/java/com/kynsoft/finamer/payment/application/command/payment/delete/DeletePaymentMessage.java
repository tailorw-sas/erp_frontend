package com.kynsoft.finamer.payment.application.command.payment.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeletePaymentMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_PAYMENT";

    public DeletePaymentMessage(UUID id) {
        this.id = id;
    }

}
