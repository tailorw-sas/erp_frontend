package com.kynsoft.finamer.settings.application.command.manageAttachmentType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageAttachmentTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_ATTACHMENT_TYPE";

    public CreateManageAttachmentTypeMessage(UUID id) {
        this.id = id;
    }
}
