package com.kynsoft.finamer.invoicing.application.command.manageInvoiceType.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageInvoiceTypeCommandHandler implements ICommandHandler<CreateManageInvoiceTypeCommand> {

    private final IManageInvoiceTypeService service;

    public CreateManageInvoiceTypeCommandHandler(IManageInvoiceTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageInvoiceTypeCommand command) {

        service.create(new ManageInvoiceTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.isInvoice(),
                command.isCredit(),
                command.isIncome(),
                command.getStatus(),
                command.isEnabledToPolicy()
        ));
    }
}
