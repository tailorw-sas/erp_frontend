package com.kynsoft.finamer.payment.application.command.paymentDetail.reverseTransaction;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateReverseTransactionMessage implements ICommandMessage {

    private UUID id;

    public CreateReverseTransactionMessage(UUID id) {
        this.id = id;
    }

}
