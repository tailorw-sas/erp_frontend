package com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.services.attachement.update.UpdateAttachmentService;
import org.springframework.stereotype.Component;

@Component
public class UpdateMasterPaymentAttachmentCommandHandler implements ICommandHandler<UpdateMasterPaymentAttachmentCommand> {

    private final UpdateAttachmentService updateAttachmentService;

    public UpdateMasterPaymentAttachmentCommandHandler(UpdateAttachmentService updateAttachmentService) {
        this.updateAttachmentService = updateAttachmentService;
    }

    @Override
    public void handle(UpdateMasterPaymentAttachmentCommand command){
        this.updateAttachmentService.update(command.getId(),
                command.getStatus(),
                command.getEmployee(),
                command.getResourceType(),
                command.getAttachmentType(),
                command.getFileName(),
                command.getFileWeight(),
                command.getPath(),
                command.getRemark());
    }
}
