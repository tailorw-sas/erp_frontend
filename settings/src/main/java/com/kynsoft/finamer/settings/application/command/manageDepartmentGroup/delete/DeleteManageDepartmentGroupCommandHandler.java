package com.kynsoft.finamer.settings.application.command.manageDepartmentGroup.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageDepartmentGroupDto;
import com.kynsoft.finamer.settings.domain.services.IManageDepartmentGroupService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageDepartmentGroupCommandHandler implements ICommandHandler<DeleteManageDepartmentGroupCommand> {

    private final IManageDepartmentGroupService service;

    public DeleteManageDepartmentGroupCommandHandler(IManageDepartmentGroupService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageDepartmentGroupCommand command) {
        ManageDepartmentGroupDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
