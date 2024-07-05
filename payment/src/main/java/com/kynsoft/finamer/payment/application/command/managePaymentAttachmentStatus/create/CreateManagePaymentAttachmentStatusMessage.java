package com.kynsoft.finamer.payment.application.command.managePaymentAttachmentStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagePaymentAttachmentStatusMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "CREATE_MANAGE_PAYMENT_ATTACHMENT_STATUS";

    public CreateManagePaymentAttachmentStatusMessage(final UUID id) {
        this.id = id;
    }
}
