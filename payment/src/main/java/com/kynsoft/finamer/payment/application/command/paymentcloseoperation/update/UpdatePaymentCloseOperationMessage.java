package com.kynsoft.finamer.payment.application.command.paymentcloseoperation.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdatePaymentCloseOperationMessage implements ICommandMessage {

    private final UUID id;

    public UpdatePaymentCloseOperationMessage(UUID id) {
        this.id = id;
    }

}
