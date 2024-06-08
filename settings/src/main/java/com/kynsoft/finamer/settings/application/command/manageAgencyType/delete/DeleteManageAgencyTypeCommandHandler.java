package com.kynsoft.finamer.settings.application.command.manageAgencyType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageAgencyTypeCommandHandler implements ICommandHandler<DeleteManageAgencyTypeCommand> {

    private final IManageAgencyTypeService service;

    public DeleteManageAgencyTypeCommandHandler(IManageAgencyTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageAgencyTypeCommand command) {
        ManageAgencyTypeDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
