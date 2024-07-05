package com.kynsoft.finamer.settings.application.command.manageNightType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageNightTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageNightTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageNightTypeCommandHandler implements ICommandHandler<DeleteManageNightTypeCommand> {

    private final IManageNightTypeService service;

    public DeleteManageNightTypeCommandHandler(IManageNightTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageNightTypeCommand command) {
        ManageNightTypeDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
