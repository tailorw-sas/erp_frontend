package com.kynsoft.finamer.invoicing.application.command.manageInvoiceTransactionType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTransactionTypeService;
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
