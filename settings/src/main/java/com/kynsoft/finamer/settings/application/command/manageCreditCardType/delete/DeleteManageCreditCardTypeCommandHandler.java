package com.kynsoft.finamer.settings.application.command.manageCreditCardType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageCreditCardTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageCreditCardTypeCommandHandler implements ICommandHandler<DeleteManageCreditCardTypeCommand> {

    private final IManageCreditCardTypeService service;

    public DeleteManageCreditCardTypeCommandHandler(IManageCreditCardTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageCreditCardTypeCommand command) {
        ManageCreditCardTypeDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
