package com.kynsoft.finamer.settings.application.command.managePaymentAttachmentStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManagePaymentAttachmentStatusMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "DELETE_PAYMENT_ATTACHMENT_STATUS";
    
    public DeleteManagePaymentAttachmentStatusMessage(final UUID id) {
        this.id = id;
    }
    
}
