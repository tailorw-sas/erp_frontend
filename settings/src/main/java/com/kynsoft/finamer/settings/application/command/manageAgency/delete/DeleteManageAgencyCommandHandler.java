package com.kynsoft.finamer.settings.application.command.manageAgency.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageAgencyCommandHandler implements ICommandHandler<DeleteManageAgencyCommand> {

    private final IManageAgencyService service;

    public DeleteManageAgencyCommandHandler(IManageAgencyService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageAgencyCommand command) {


        service.delete(command.getId());
    }
}
