package com.kynsoft.finamer.creditcard.application.command.manageBank.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerBankDto;
import com.kynsoft.finamer.creditcard.domain.services.IManagerBankService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerBankCommandHandler implements ICommandHandler<CreateManagerBankCommand> {

    private final IManagerBankService service;

    public CreateManagerBankCommandHandler(IManagerBankService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagerBankCommand command) {
        service.create(new ManagerBankDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getStatus()
        ));
    }
}
