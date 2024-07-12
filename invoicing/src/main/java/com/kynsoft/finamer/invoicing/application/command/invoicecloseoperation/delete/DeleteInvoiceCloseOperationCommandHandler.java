package com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceCloseOperationDto;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceCloseOperationService;
import org.springframework.stereotype.Component;

@Component
public class DeleteInvoiceCloseOperationCommandHandler implements ICommandHandler<DeleteInvoiceCloseOperationCommand> {

    private final IInvoiceCloseOperationService service;

    public DeleteInvoiceCloseOperationCommandHandler(IInvoiceCloseOperationService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteInvoiceCloseOperationCommand command) {
        InvoiceCloseOperationDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
