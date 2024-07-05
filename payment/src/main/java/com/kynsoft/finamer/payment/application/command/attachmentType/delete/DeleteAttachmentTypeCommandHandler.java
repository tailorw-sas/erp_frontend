package com.kynsoft.finamer.payment.application.command.attachmentType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteAttachmentTypeCommandHandler implements ICommandHandler<DeleteAttachmentTypeCommand> {

    private final IManageAttachmentTypeService service;

    public DeleteAttachmentTypeCommandHandler(IManageAttachmentTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteAttachmentTypeCommand command) {
        AttachmentTypeDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
