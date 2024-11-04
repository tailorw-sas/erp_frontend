package com.kynsoft.finamer.invoicing.application.command.manageInvoiceTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageInvoiceTransactionTypeCommandHandler implements ICommandHandler<CreateManageInvoiceTransactionTypeCommand> {

    private final IManageInvoiceTransactionTypeService service;

    public CreateManageInvoiceTransactionTypeCommandHandler(IManageInvoiceTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageInvoiceTransactionTypeCommand command) {


        service.create(new ManageInvoiceTransactionTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.isDefaults()
                ));
    }
}
