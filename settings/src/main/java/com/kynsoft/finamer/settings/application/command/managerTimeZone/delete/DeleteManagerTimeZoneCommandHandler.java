package com.kynsoft.finamer.settings.application.command.managerTimeZone.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagerTimeZoneDto;
import com.kynsoft.finamer.settings.domain.services.IManagerTimeZoneService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerTimeZoneCommandHandler implements ICommandHandler<DeleteManagerTimeZoneCommand> {

    private final IManagerTimeZoneService service;

    public DeleteManagerTimeZoneCommandHandler(IManagerTimeZoneService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagerTimeZoneCommand command) {
        ManagerTimeZoneDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
