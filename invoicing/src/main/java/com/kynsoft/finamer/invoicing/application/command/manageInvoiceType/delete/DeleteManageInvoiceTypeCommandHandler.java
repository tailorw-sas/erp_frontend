package com.kynsoft.finamer.invoicing.application.command.manageInvoiceType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageInvoiceTypeCommandHandler implements ICommandHandler<DeleteManageInvoiceTypeCommand> {

    private final IManageInvoiceTypeService service;

    public DeleteManageInvoiceTypeCommandHandler(IManageInvoiceTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageInvoiceTypeCommand command) {
        ManageInvoiceTypeDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
