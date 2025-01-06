package com.kynsoft.finamer.insis.application.command.manageAgency.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.insis.domain.services.IManageAgencyService;
import org.springframework.stereotype.Component;

@Component
public class DeleteAgencyCommandHandler implements ICommandHandler<DeleteAgencyCommand> {
    private final IManageAgencyService service;

    public DeleteAgencyCommandHandler(IManageAgencyService service){
        this.service = service;
    }

    @Override
    public void handle(DeleteAgencyCommand command) {
        ManageAgencyDto dto = service.findById(command.getId());
        service.delete(dto);
    }
}
