package com.kynsoft.finamer.invoicing.application.command.manageAttachment.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentService;
import org.springframework.stereotype.Component;

@Component
public class DeleteAttachmentCommandHandler implements ICommandHandler<DeleteAttachmentCommand> {

    private final IManageAttachmentService service;

    public DeleteAttachmentCommandHandler(IManageAttachmentService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteAttachmentCommand command) {
        ManageAttachmentDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
