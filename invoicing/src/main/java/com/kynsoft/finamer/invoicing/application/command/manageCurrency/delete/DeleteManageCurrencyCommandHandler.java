package com.kynsoft.finamer.invoicing.application.command.manageCurrency.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageCurrencyDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageCurrencyService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageCurrencyCommandHandler implements ICommandHandler<DeleteManageCurrencyCommand> {

    private final IManageCurrencyService service;

    public DeleteManageCurrencyCommandHandler(IManageCurrencyService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageCurrencyCommand command) {
        ManageCurrencyDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
