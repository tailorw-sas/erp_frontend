package com.kynsoft.finamer.settings.application.command.manageCityState.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.settings.domain.services.IManageCityStateService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageCityStateCommandHandler implements ICommandHandler<DeleteManageCityStateCommand> {

    private final IManageCityStateService service;

    public DeleteManageCityStateCommandHandler(IManageCityStateService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageCityStateCommand command) {
        ManageCityStateDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
