package com.kynsoft.finamer.creditcard.application.command.manageAccountType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerAccountTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManagerAccountTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerAccountTypeCommandHandler implements ICommandHandler<DeleteManagerAccountTypeCommand> {

    private final IManagerAccountTypeService service;

    public DeleteManagerAccountTypeCommandHandler(IManagerAccountTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagerAccountTypeCommand command) {
        ManagerAccountTypeDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
