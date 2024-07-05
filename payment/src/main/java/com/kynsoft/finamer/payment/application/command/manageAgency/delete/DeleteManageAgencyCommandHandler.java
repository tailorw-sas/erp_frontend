package com.kynsoft.finamer.payment.application.command.manageAgency.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageAgencyCommandHandler implements ICommandHandler<DeleteManageAgencyCommand> {

    private final IManageAgencyService service;

    public DeleteManageAgencyCommandHandler(IManageAgencyService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageAgencyCommand command) {
        ManageAgencyDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
