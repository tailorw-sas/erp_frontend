package com.kynsoft.finamer.creditcard.application.command.manageCreditCardType.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageCreditCardTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageCreditCardTypeCommandHandler implements ICommandHandler<CreateManageCreditCardTypeCommand> {

    private final IManageCreditCardTypeService service;

    public CreateManageCreditCardTypeCommandHandler(IManageCreditCardTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageCreditCardTypeCommand command) {
        service.create(new ManageCreditCardTypeDto(
                command.getId(),
                command.getCode(),
                command.getName()
        ));
    }
}
