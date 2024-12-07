package com.kynsoft.finamer.creditcard.application.command.managePaymentTransactionStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.services.IManagePaymentTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagePaymentTransactionStatusCommandHandler implements ICommandHandler<DeleteManagePaymentTransactionStatusCommand> {

    private final IManagePaymentTransactionStatusService service;

    public DeleteManagePaymentTransactionStatusCommandHandler(IManagePaymentTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagePaymentTransactionStatusCommand command) {
        this.service.delete(command.getId());
    }
}
