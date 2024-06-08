package com.kynsoft.finamer.settings.application.command.manageEmployee.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageEmployeeCommandHandler implements ICommandHandler<DeleteManageEmployeeCommand> {

    private final IManageEmployeeService service;

    public DeleteManageEmployeeCommandHandler(IManageEmployeeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageEmployeeCommand command) {
        ManageEmployeeDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
