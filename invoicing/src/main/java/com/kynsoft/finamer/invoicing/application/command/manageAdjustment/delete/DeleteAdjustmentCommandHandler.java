package com.kynsoft.finamer.invoicing.application.command.manageAdjustment.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAdjustmentService;
import org.springframework.stereotype.Component;

@Component
public class DeleteAdjustmentCommandHandler implements ICommandHandler<DeleteAdjustmentCommand> {

    private final IManageAdjustmentService service;

    public DeleteAdjustmentCommandHandler(IManageAdjustmentService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteAdjustmentCommand command) {
        ManageAdjustmentDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
