package com.kynsoft.finamer.settings.application.command.managerCountry.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.settings.domain.services.IManagerCountryService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerCountryCommandHandler implements ICommandHandler<DeleteManagerCountryCommand> {

    private final IManagerCountryService service;

    public DeleteManagerCountryCommandHandler(IManagerCountryService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagerCountryCommand command) {
        ManagerCountryDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
