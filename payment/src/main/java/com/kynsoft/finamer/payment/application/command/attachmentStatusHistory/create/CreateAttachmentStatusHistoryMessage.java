package com.kynsoft.finamer.payment.application.command.attachmentStatusHistory.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class CreateAttachmentStatusHistoryMessage implements ICommandMessage {

    private UUID id;

    public CreateAttachmentStatusHistoryMessage(UUID id) {
        this.id = id;
    }

}
