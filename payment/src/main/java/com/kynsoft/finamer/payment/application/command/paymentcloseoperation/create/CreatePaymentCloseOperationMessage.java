package com.kynsoft.finamer.payment.application.command.paymentcloseoperation.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class CreatePaymentCloseOperationMessage implements ICommandMessage {

    private UUID id;

    public CreatePaymentCloseOperationMessage(UUID id) {
        this.id = id;
    }

}
