package com.kynsoft.finamer.creditcard.application.command.managerTransactionStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageTransactionStatusCommandHandler implements ICommandHandler<CreateManageTransactionStatusCommand> {

    private final IManageTransactionStatusService service;

    public CreateManageTransactionStatusCommandHandler(IManageTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageTransactionStatusCommand command) {
        service.create(new ManageTransactionStatusDto(
                command.getId(),
                command.getCode(),
                command.getName()
        ));
    }
}
