package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingAmount;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import org.springframework.stereotype.Component;

@Component
public class UpdateBookingCalculateBookingAmountCommandHandler implements ICommandHandler<UpdateBookingCalculateBookingAmountCommand> {

    public UpdateBookingCalculateBookingAmountCommandHandler() {
    }

    @Override
    public void handle(UpdateBookingCalculateBookingAmountCommand command) {

        double total = command.getBookingDto().getRoomRates().stream()
                .mapToDouble(ManageRoomRateDto::getInvoiceAmount)
                .sum();
        command.getBookingDto().setInvoiceAmount(total);
    }

}
