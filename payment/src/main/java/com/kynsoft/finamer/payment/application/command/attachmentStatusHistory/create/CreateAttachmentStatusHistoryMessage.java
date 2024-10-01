package com.kynsoft.finamer.payment.application.command.attachmentStatusHistory.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateAttachmentStatusHistoryMessage implements ICommandMessage {

    private UUID id;

    public CreateAttachmentStatusHistoryMessage(UUID id) {
        this.id = id;
    }

}
