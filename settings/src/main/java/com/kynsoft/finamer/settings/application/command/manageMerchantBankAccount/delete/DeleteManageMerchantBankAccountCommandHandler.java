package com.kynsoft.finamer.settings.application.command.manageMerchantBankAccount.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantBankAccountDto;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantBankAccountService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageMerchantBankAccountCommandHandler implements ICommandHandler<DeleteManageMerchantBankAccountCommand> {

    private final IManageMerchantBankAccountService service;

    public DeleteManageMerchantBankAccountCommandHandler(IManageMerchantBankAccountService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageMerchantBankAccountCommand command) {
        ManageMerchantBankAccountDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
