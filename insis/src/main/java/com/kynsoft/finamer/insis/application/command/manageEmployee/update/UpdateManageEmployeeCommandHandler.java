package com.kynsoft.finamer.insis.application.command.manageEmployee.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.insis.domain.services.IManageEmployeeService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageEmployeeCommandHandler implements ICommandHandler<UpdateManageEmployeeCommand> {

    private final IManageEmployeeService service;

    public UpdateManageEmployeeCommandHandler(IManageEmployeeService service){
        this.service = service;
    }

    @Override
    public void handle(UpdateManageEmployeeCommand command) {

        ManageEmployeeDto dto = new ManageEmployeeDto(
                command.getId(),
                command.getFirstName(),
                command.getLastName(),
                command.getEmail(),
                command.getUpdatedAt()
        );

        service.update(dto);
    }
}
