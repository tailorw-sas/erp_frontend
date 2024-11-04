package com.kynsoft.finamer.creditcard.application.command.manageBank.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerBankDto;
import com.kynsoft.finamer.creditcard.domain.services.IManagerBankService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerBankCommandHandler implements ICommandHandler<DeleteManagerBankCommand> {

    private final IManagerBankService service;

    public DeleteManagerBankCommandHandler(IManagerBankService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagerBankCommand command) {
        ManagerBankDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
