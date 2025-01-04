package com.kynsoft.finamer.invoicing.application.command.manageCurrency.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageCurrencyDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageCurrencyService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageCurrencyCommandHandler implements ICommandHandler<CreateManageCurrencyCommand> {

    private final IManageCurrencyService service;

    public CreateManageCurrencyCommandHandler(IManageCurrencyService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageCurrencyCommand command) {
        service.create(new ManageCurrencyDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus()
        ));
    }
}
