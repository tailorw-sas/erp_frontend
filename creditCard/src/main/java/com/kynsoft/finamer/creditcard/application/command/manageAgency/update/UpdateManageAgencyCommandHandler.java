package com.kynsoft.finamer.creditcard.application.command.manageAgency.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageAgencyService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageAgencyCommandHandler implements ICommandHandler<UpdateManageAgencyCommand> {

    private final IManageAgencyService service;

    public UpdateManageAgencyCommandHandler(IManageAgencyService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageAgencyCommand command) {
        ManageAgencyDto dto = service.findById(command.getId());
        dto.setName(command.getName());
        service.update(dto);
    }
}