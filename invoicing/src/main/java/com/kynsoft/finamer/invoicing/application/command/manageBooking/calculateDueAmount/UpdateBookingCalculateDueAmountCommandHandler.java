package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateDueAmount;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class UpdateBookingCalculateDueAmountCommandHandler implements ICommandHandler<UpdateBookingCalculateDueAmountCommand> {

    public UpdateBookingCalculateDueAmountCommandHandler() {
    }

    @Override
    public void handle(UpdateBookingCalculateDueAmountCommand command) {

        command.getBookingDto().setDueAmount(command.getBookingDto().getDueAmount() + command.getRateAmount());
    }

}
