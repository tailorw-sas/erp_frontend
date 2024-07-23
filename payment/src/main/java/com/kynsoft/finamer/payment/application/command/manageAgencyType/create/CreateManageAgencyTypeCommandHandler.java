package com.kynsoft.finamer.payment.application.command.manageAgencyType.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageAgencyTypeCommandHandler implements ICommandHandler<CreateManageAgencyTypeCommand> {

    private final IManageAgencyTypeService service;

    public CreateManageAgencyTypeCommandHandler(IManageAgencyTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageAgencyTypeCommand command) {

        service.create(new ManageAgencyTypeDto(
                command.getId(),
                command.getCode(),
                command.getStatus(),
                command.getName()
        ));
    }
}
