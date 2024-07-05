package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteMasterPaymentAttachmentMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MASTER_PAYMENT_ATTACHMENT";

    public DeleteMasterPaymentAttachmentMessage(UUID id) {
        this.id = id;
    }

}
