package com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagePaymentAttachmentStatusMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_MANAGE_PAYMENT_ATTACHMENT_STATUS";
    
    public UpdateManagePaymentAttachmentStatusMessage(final UUID id) {
        this.id = id;
    }
}
