package com.kynsoft.finamer.invoicing.application.command.income.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.IncomeDto;
import com.kynsoft.finamer.invoicing.domain.services.IIncomeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteIncomeCommandHandler implements ICommandHandler<DeleteIncomeCommand> {

    private final IIncomeService service;

    public DeleteIncomeCommandHandler(IIncomeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteIncomeCommand command) {
        IncomeDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
