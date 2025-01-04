package com.kynsoft.finamer.creditcard.application.command.attachment.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateAttachmentMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_ATTACHMENT";

    public CreateAttachmentMessage(UUID id) {
        this.id = id;
    }

}
