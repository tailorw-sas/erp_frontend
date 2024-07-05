package com.kynsoft.finamer.invoicing.application.command.manageInvoice.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import org.springframework.stereotype.Component;

@Component
public class DeleteInvoiceCommandHandler implements ICommandHandler<DeleteInvoiceCommand> {

    private final IManageInvoiceService service;

    public DeleteInvoiceCommandHandler(IManageInvoiceService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteInvoiceCommand command) {
        ManageInvoiceDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
