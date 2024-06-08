package com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageInvoiceTransactionTypeCommandHandler implements ICommandHandler<DeleteManageInvoiceTransactionTypeCommand> {

    private final IManageInvoiceTransactionTypeService service;

    public DeleteManageInvoiceTransactionTypeCommandHandler(IManageInvoiceTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageInvoiceTransactionTypeCommand command) {
        ManageInvoiceTransactionTypeDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
