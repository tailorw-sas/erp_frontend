package com.kynsoft.finamer.invoicing.application.command.manageEmployee.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageEmployeeService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageEmployeeCommandHandler implements ICommandHandler<UpdateManageEmployeeCommand> {

    private final IManageEmployeeService service;

    public UpdateManageEmployeeCommandHandler(IManageEmployeeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageEmployeeCommand command) {
    
        ManageEmployeeDto dto = this.service.findById(command.getId());
        dto.setEmail(command.getEmail());
        dto.setFirstName(command.getFirstName());
        dto.setLastName(command.getLastName());
        dto.setPhoneExtension(command.getPhoneExtension());

        service.update(dto);
    }
}
