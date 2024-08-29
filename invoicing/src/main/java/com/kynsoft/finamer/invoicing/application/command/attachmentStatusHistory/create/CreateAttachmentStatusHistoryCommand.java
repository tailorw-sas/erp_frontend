package com.kynsoft.finamer.invoicing.application.command.attachmentStatusHistory.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateAttachmentStatusHistoryCommand implements ICommand {

    private UUID id;
    @Override
    public CreateAttachmentStatusHistoryMessage getMessage() {
        return new CreateAttachmentStatusHistoryMessage();
    }
}
