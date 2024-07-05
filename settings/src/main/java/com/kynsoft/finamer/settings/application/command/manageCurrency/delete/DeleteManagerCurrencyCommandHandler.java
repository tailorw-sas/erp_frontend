package com.kynsoft.finamer.settings.application.command.manageCurrency.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagerCurrencyDto;
import com.kynsoft.finamer.settings.domain.services.IManagerCurrencyService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerCurrencyCommandHandler implements ICommandHandler<DeleteManagerCurrencyCommand> {

    private final IManagerCurrencyService service;

    public DeleteManagerCurrencyCommandHandler(IManagerCurrencyService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagerCurrencyCommand command) {
        ManagerCurrencyDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
