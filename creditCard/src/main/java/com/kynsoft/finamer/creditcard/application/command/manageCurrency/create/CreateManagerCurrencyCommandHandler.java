package com.kynsoft.finamer.creditcard.application.command.manageCurrency.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerCurrencyDto;
import com.kynsoft.finamer.creditcard.domain.services.IManagerCurrencyService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerCurrencyCommandHandler implements ICommandHandler<CreateManagerCurrencyCommand> {

    private final IManagerCurrencyService service;

    public CreateManagerCurrencyCommandHandler(IManagerCurrencyService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagerCurrencyCommand command) {
        service.create(new ManagerCurrencyDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus()
        ));
    }
}
