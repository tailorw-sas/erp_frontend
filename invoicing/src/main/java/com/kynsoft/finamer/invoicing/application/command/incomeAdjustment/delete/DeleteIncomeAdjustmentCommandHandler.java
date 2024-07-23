package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.IncomeAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.services.IIncomeAdjustmentService;
import org.springframework.stereotype.Component;

@Component
public class DeleteIncomeAdjustmentCommandHandler implements ICommandHandler<DeleteIncomeAdjustmentCommand> {

    private final IIncomeAdjustmentService service;

    public DeleteIncomeAdjustmentCommandHandler(IIncomeAdjustmentService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteIncomeAdjustmentCommand command) {
        IncomeAdjustmentDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
