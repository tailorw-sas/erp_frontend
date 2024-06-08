package com.kynsof.identity.application.command.businessModule.delete;

import com.kynsof.identity.domain.dto.BusinessModuleDto;
import com.kynsof.identity.domain.interfaces.service.IBusinessModuleService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class DeleteBusinessModuleCommandHandler implements ICommandHandler<DeleteBusinessModuleCommand> {

    private final IBusinessModuleService service;

    public DeleteBusinessModuleCommandHandler(IBusinessModuleService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteBusinessModuleCommand command) {
        BusinessModuleDto delete = this.service.findById(command.getId());
        service.delete(delete);
    }

}
