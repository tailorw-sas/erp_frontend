package com.kynsoft.finamer.payment.application.command.attachmentType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateAttachmentTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_ATTACHMENT_TYPE";

    public UpdateAttachmentTypeMessage(UUID id) {
        this.id = id;
    }

}
