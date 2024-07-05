package com.kynsoft.finamer.invoicing.application.command.manageAttachment.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateAttachmentMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_ATTACHMENT";

    public UpdateAttachmentMessage(UUID id) {
        this.id = id;
    }

}
