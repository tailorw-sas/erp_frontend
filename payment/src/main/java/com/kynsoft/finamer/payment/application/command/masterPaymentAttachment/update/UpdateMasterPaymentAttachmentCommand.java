package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateMasterPaymentAttachmentCommand implements ICommand {
    private UUID id;
    private Status status;
    private UUID resource;
    private UUID resourceType;
    private UUID attachmentType;
    private String fileName;
    private String path;
    private String remark;

    public UpdateMasterPaymentAttachmentCommand(UUID id, Status status, UUID resource, 
                                                UUID resourceType, UUID attachmentType, 
                                                String fileName, String path, String remark) {
        this.id = id;
        this.status = status;
        this.resource = resource;
        this.resourceType = resourceType;
        this.attachmentType = attachmentType;
        this.fileName = fileName;
        this.path = path;
        this.remark = remark;
    }

    public static UpdateMasterPaymentAttachmentCommand fromRequest(UpdateMasterPaymentAttachmentRequest request, UUID id) {
        return new UpdateMasterPaymentAttachmentCommand(
                id,
                request.getStatus(),
                request.getResource(),
                request.getResourceType(),
                request.getAttachmentType(),
                request.getFileName(),
                request.getPath(),
                request.getRemark()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateMasterPaymentAttachmentMessage(id);
    }
}
