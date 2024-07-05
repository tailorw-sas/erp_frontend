package com.kynsoft.finamer.invoicing.application.command.manageAttachment.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateAttachmentCommand implements ICommand {

    private UUID id;
    private String filename;
    private String file;
    private String remark;
    private UUID type;

    public static UpdateAttachmentCommand fromRequest(UpdateAttachmentRequest request, UUID id) {
        return new UpdateAttachmentCommand(
                id,
                request.getFilename(),
                request.getFile(),
                request.getRemark(),
                request.getType());
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateAttachmentMessage(id);
    }
}
