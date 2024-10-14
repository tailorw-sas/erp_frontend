package com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.calculateInvoiceAmount;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import org.springframework.stereotype.Component;

@Component
public class UpdateInvoiceCalculateInvoiceAmountCommandHandler implements ICommandHandler<UpdateInvoiceCalculateInvoiceAmountCommand> {

    public UpdateInvoiceCalculateInvoiceAmountCommandHandler() {
    }

    @Override
    public void handle(UpdateInvoiceCalculateInvoiceAmountCommand command) {

        double total = command.getInvoiceDto().getBookings().stream()
                .mapToDouble(ManageBookingDto::getInvoiceAmount)
                .sum();
        command.getInvoiceDto().setInvoiceAmount(total);
    }

}
