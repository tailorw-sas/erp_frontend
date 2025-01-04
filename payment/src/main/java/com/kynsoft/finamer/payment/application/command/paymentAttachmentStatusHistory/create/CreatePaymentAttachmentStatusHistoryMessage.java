package com.kynsoft.finamer.payment.application.command.paymentAttachmentStatusHistory.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentAttachmentStatusHistoryMessage implements ICommandMessage {

    private UUID id;

    public CreatePaymentAttachmentStatusHistoryMessage(UUID id) {
        this.id = id;
    }

}
