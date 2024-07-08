package com.kynsoft.finamer.payment.application.command.paymentcloseoperation.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeletePaymentCloseOperationMessage implements ICommandMessage {

    private final UUID id;

    public DeletePaymentCloseOperationMessage(UUID id) {
        this.id = id;
    }

}
