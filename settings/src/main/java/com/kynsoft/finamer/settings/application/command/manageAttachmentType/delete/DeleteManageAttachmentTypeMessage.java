package com.kynsoft.finamer.settings.application.command.manageAttachmentType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import java.util.UUID;

@Getter
public class DeleteManageAttachmentTypeMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "DELETE_MANAGE_ATTACHMENT_TYPE";

    public DeleteManageAttachmentTypeMessage(UUID id) {
        this.id = id;
    }
}
