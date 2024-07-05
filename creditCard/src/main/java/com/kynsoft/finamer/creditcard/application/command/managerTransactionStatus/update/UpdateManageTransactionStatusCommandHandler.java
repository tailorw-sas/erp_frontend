package com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageTransactionStatusCommandHandler implements ICommandHandler<UpdateManageTransactionStatusCommand> {

    private final IManageTransactionStatusService service;

    public UpdateManageTransactionStatusCommandHandler(IManageTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageTransactionStatusCommand command) {
        ManageTransactionStatusDto dto = this.service.findById(command.getId());

        this.service.update(dto);
    }

}
