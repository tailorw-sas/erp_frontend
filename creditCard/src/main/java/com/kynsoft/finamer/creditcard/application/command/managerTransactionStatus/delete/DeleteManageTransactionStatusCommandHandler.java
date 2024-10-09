package com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageTransactionStatusCommandHandler implements ICommandHandler<DeleteManageTransactionStatusCommand> {

    private final IManageTransactionStatusService service;

    public DeleteManageTransactionStatusCommandHandler(IManageTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageTransactionStatusCommand command) {
        ManageTransactionStatusDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
