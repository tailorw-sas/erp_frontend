package com.kynsoft.finamer.invoicing.application.command.manageAttachmentType.create;


import com.kynsof.share.core.domain.bus.command.ICommandHandler;

import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentTypeDto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentTypeService;

import org.springframework.stereotype.Component;

@Component
public class CreateManageAttachmentTypeCommandHandler implements ICommandHandler<CreateManageAttachmentTypeCommand> {

    private final IManageAttachmentTypeService service;

    public CreateManageAttachmentTypeCommandHandler(IManageAttachmentTypeService service) {
        this.service = service;

    }

    @Override
    public void handle(CreateManageAttachmentTypeCommand command) {

        service.create(new ManageAttachmentTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                Status.valueOf(command.getStatus()),
                command.getDefaults(),
                command.isAttachInvDefault()
        ));

    }
}
