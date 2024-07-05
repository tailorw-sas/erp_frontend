package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateMasterPaymentAttachmentMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MASTER_PAYMENT_ATTACHMENT";

    public CreateMasterPaymentAttachmentMessage(UUID id) {
        this.id = id;
    }

}
