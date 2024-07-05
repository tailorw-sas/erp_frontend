package com.kynsoft.finamer.settings.application.command.manageBankAccount.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.settings.domain.services.IManageBankAccountService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageBankAccountCommandHandler implements ICommandHandler<DeleteManageBankAccountCommand> {

    private final IManageBankAccountService service;

    public DeleteManageBankAccountCommandHandler(IManageBankAccountService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageBankAccountCommand command) {
        ManageBankAccountDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
