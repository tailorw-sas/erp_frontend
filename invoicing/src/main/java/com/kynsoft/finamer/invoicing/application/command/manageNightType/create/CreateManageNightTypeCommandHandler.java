package com.kynsoft.finamer.invoicing.application.command.manageNightType.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageNightTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageNightTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageNightTypeCommandHandler implements ICommandHandler<CreateManageNightTypeCommand> {

    private final IManageNightTypeService service;

    public CreateManageNightTypeCommandHandler(IManageNightTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageNightTypeCommand command) {

        service.create(new ManageNightTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus()
        ));
    }
}
