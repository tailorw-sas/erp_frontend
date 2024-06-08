package com.kynsoft.finamer.settings.application.command.managePaymentTransactionStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionStatusService;
import org.springframework.stereotype.Component;

@Component
public class DeletePaymentTransactionStatusCommandHandler implements ICommandHandler<DeleteManagePaymentTransactionStatusCommand> {

    private final IManagePaymentTransactionStatusService service;

    public DeletePaymentTransactionStatusCommandHandler(IManagePaymentTransactionStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagePaymentTransactionStatusCommand command) {
        ManagePaymentTransactionStatusDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
