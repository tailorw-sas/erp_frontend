package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateMasterPaymentAttachmentMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MASTER_PAYMENT_ATTACHMENT";

    public UpdateMasterPaymentAttachmentMessage(UUID id) {
        this.id = id;
    }

}
