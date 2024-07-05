package com.kynsoft.finamer.invoicing.application.command.manageInvoiceStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageInvoiceStatusCommandHandler implements ICommandHandler<DeleteManageInvoiceStatusCommand> {

    private final IManageInvoiceStatusService service;

    public DeleteManageInvoiceStatusCommandHandler(IManageInvoiceStatusService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageInvoiceStatusCommand command) {
        ManageInvoiceStatusDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}
