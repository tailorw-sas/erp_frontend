package com.kynsoft.finamer.settings.application.command.managePaymentTransactionType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagePaymentTransactionTypeCommandHandler implements ICommandHandler<DeleteManagePaymentTransactionTypeCommand> {

    private final IManagePaymentTransactionTypeService service;

    public DeleteManagePaymentTransactionTypeCommandHandler(IManagePaymentTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagePaymentTransactionTypeCommand command) {
        ManagePaymentTransactionTypeDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
