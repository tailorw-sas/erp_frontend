package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.calculateDueAmount;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class UpdateInvoiceCalculateDueAmountCommandHandler implements ICommandHandler<UpdateInvoiceCalculateDueAmountCommand> {

    public UpdateInvoiceCalculateDueAmountCommandHandler() {
    }

    @Override
    public void handle(UpdateInvoiceCalculateDueAmountCommand command) {

        command.getInvoiceDto().setDueAmount(command.getInvoiceDto().getDueAmount() + command.getDueAmount());
    }

}
