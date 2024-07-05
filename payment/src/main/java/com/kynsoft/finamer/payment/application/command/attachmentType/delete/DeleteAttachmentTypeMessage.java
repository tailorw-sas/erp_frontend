package com.kynsoft.finamer.payment.application.command.attachmentType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteAttachmentTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_ATTACHMENT_TYPE";

    public DeleteAttachmentTypeMessage(UUID id) {
        this.id = id;
    }

}
