package com.kynsoft.finamer.payment.application.command.manageBankAccount.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.payment.domain.services.IManageBankAccountService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageBankAccountCommandHandler implements ICommandHandler<UpdateManageBankAccountCommand> {

    private final IManageBankAccountService service;

    public UpdateManageBankAccountCommandHandler(IManageBankAccountService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageBankAccountCommand command) {

        ManageBankAccountDto dto = this.service.findById(command.getId());
        dto.setNameOfBank(command.getNameOfBank());
        dto.setStatus(command.getStatus());
        dto.setAccountNumber(command.getAccountNumber());
        service.update(dto);
    }
}
