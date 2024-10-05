package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingAmount;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateBookingCalculateBookingAmountCommand implements ICommand {

    private UUID bookingId;

    public UpdateBookingCalculateBookingAmountCommand(UUID bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateBookingCalculateBookingAmountMessage(bookingId);
    }
}
