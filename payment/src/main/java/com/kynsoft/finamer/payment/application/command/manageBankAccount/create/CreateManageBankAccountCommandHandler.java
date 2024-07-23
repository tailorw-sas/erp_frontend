package com.kynsoft.finamer.payment.application.command.manageBankAccount.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.payment.domain.services.IManageBankAccountService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageBankAccountCommandHandler implements ICommandHandler<CreateManageBankAccountCommand> {

    private final IManageBankAccountService service;

    public CreateManageBankAccountCommandHandler(IManageBankAccountService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageBankAccountCommand command) {

        service.create(new ManageBankAccountDto(command.getId(), command.getAccountNumber(), command.getStatus()));

    }
}
