package com.kynsoft.finamer.invoicing.application.command.attachmentStatusHistory.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateAttachmentStatusHistoryMessage implements ICommandMessage {

    private final String command = "CREATE_ATTACHMENT_STATUS_HISTORY";

    public CreateAttachmentStatusHistoryMessage() {
    }
}
