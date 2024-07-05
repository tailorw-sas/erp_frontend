package com.kynsoft.finamer.payment.application.command.manageResourceType.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageResourceTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManageResourceTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageResourceTypeCommandHandler implements ICommandHandler<CreateManageResourceTypeCommand> {

    private final IManageResourceTypeService service;

    public CreateManageResourceTypeCommandHandler(IManageResourceTypeService service) {
        this.service = service;

    }

    @Override
    public void handle(CreateManageResourceTypeCommand command) {

        service.create(new ManageResourceTypeDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getStatus()
        ));
    }
}
