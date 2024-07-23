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
    private UUID employee;
    private UUID resourceType;
    private UUID attachmentType;
    private String fileName;
    private String fileWeight;
    private String path;
    private String remark;

    public UpdateMasterPaymentAttachmentCommand(UUID id, Status status, UUID employee, UUID resourceType, UUID attachmentType, 
                                                String fileName, String path, String remark, String fileWeight) {
        this.id = id;
        this.status = status;
        this.employee = employee;
        this.resourceType = resourceType;
        this.attachmentType = attachmentType;
        this.fileName = fileName;
        this.path = path;
        this.remark = remark;
        this.fileWeight = fileWeight;
    }

    public static UpdateMasterPaymentAttachmentCommand fromRequest(UpdateMasterPaymentAttachmentRequest request, UUID id) {
        return new UpdateMasterPaymentAttachmentCommand(
                id,
                request.getStatus(),
                request.getEmployee(),
                request.getResourceType(),
                request.getAttachmentType(),
                request.getFileName(),
                request.getPath(),
                request.getRemark(),
                request.getFileWeight()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateMasterPaymentAttachmentMessage(id);
    }
}
