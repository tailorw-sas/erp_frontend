package com.kynsoft.finamer.settings.application.command.manageAttachmentType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageAttachmentTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageAttachmentTypeCommandHandler implements ICommandHandler<DeleteManageAttachmentTypeCommand> {

    private final IManageAttachmentTypeService service;

    public DeleteManageAttachmentTypeCommandHandler(IManageAttachmentTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageAttachmentTypeCommand command) {
        ManageAttachmentTypeDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
