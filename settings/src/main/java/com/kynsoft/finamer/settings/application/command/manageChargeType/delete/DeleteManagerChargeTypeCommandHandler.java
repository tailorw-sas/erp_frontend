package com.kynsoft.finamer.settings.application.command.manageChargeType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagerChargeTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManagerChargeTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerChargeTypeCommandHandler implements ICommandHandler<DeleteManagerChargeTypeCommand> {

    private final IManagerChargeTypeService service;

    public DeleteManagerChargeTypeCommandHandler(IManagerChargeTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagerChargeTypeCommand command) {
        ManagerChargeTypeDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
