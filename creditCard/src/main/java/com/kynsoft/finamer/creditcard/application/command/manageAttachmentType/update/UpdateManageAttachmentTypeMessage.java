package com.kynsoft.finamer.creditcard.application.command.manageAttachmentType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageAttachmentTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_ATTACHMENT_TYPE";

    public UpdateManageAttachmentTypeMessage(UUID id) {
        this.id = id;
    }
}
