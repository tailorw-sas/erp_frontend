package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.payment.application.services.attachement.create.CreateAttachmentService;

import org.springframework.stereotype.Component;

@Component
public class CreateMasterPaymentAttachmentCommandHandler implements ICommandHandler<CreateMasterPaymentAttachmentCommand> {

    private final CreateAttachmentService createAttachmentService;

    public CreateMasterPaymentAttachmentCommandHandler(CreateAttachmentService createAttachmentService){
        this.createAttachmentService = createAttachmentService;
    }

    @Override
    public void handle(CreateMasterPaymentAttachmentCommand command){
        CreateAttachmentRequest attachmentRequest = this.getCreateAttachmentRequest(command);

        this.createAttachmentService.create(command.getResource(),
                attachmentRequest);
    }

    private CreateAttachmentRequest getCreateAttachmentRequest(CreateMasterPaymentAttachmentCommand command){
        return new CreateAttachmentRequest(
                command.getStatus(),
                command.getEmployee(),
                command.getResourceType(),
                command.getAttachmentType(),
                command.getFileName(),
                command.getFileWeight(),
                command.getPath(),
                command.getRemark(),
                false
        );
    }
}
