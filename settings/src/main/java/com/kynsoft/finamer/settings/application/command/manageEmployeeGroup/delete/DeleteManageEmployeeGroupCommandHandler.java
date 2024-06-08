package com.kynsoft.finamer.settings.application.command.manageEmployeeGroup.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeGroupDto;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeGroupService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageEmployeeGroupCommandHandler implements ICommandHandler<DeleteManageEmployeeGroupCommand> {

    private final IManageEmployeeGroupService service;

    public DeleteManageEmployeeGroupCommandHandler(IManageEmployeeGroupService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageEmployeeGroupCommand command) {
        ManageEmployeeGroupDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
