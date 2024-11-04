package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.originalAmount;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.services.*;
import org.springframework.stereotype.Component;

@Component
public class UpdateInvoiceOriginalAmountCommandHandler implements ICommandHandler<UpdateInvoiceOriginalAmountCommand> {

    private final IManageInvoiceService service;

    public UpdateInvoiceOriginalAmountCommandHandler(IManageInvoiceService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateInvoiceOriginalAmountCommand command) {

        ManageInvoiceDto dto = this.service.findById(command.getId());
        dto.setOriginalAmount(command.getOriginalAmount());
        this.service.update(dto);
    }
}
