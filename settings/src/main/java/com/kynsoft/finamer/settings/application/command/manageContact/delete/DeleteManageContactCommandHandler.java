package com.kynsoft.finamer.settings.application.command.manageContact.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageContactDto;
import com.kynsoft.finamer.settings.domain.services.IManageContactService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageContactCommandHandler implements ICommandHandler<DeleteManageContactCommand> {

    private final IManageContactService service;

    public DeleteManageContactCommandHandler(IManageContactService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageContactCommand command) {
        ManageContactDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
