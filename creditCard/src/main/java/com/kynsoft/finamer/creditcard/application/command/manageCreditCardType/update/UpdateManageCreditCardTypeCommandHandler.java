package com.kynsoft.finamer.creditcard.application.command.manageCreditCardType.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageCreditCardTypeService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageCreditCardTypeCommandHandler implements ICommandHandler<UpdateManageCreditCardTypeCommand> {

    private final IManageCreditCardTypeService service;

    public UpdateManageCreditCardTypeCommandHandler(IManageCreditCardTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageCreditCardTypeCommand command) {
        ManageCreditCardTypeDto test = this.service.findById(command.getId());
        test.setName(command.getName());
        this.service.update(test);

    }
}
