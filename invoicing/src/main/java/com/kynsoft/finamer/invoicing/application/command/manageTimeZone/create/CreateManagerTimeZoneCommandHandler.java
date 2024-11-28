package com.kynsoft.finamer.invoicing.application.command.manageTimeZone.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManagerTimeZoneDto;
import com.kynsoft.finamer.invoicing.domain.services.IManagerTimeZoneService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerTimeZoneCommandHandler implements ICommandHandler<CreateManagerTimeZoneCommand> {

    private final IManagerTimeZoneService service;

    public CreateManagerTimeZoneCommandHandler(IManagerTimeZoneService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagerTimeZoneCommand command) {

        service.create(new ManagerTimeZoneDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getElapse(),
                command.getStatus()
        ));
    }
}
