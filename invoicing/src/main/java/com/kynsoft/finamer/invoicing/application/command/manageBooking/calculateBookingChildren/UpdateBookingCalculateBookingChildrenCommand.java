package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingChildren;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateBookingCalculateBookingChildrenCommand implements ICommand {

    private UUID bookingId;

    public UpdateBookingCalculateBookingChildrenCommand(UUID bookingId) {
        this.bookingId = bookingId;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateBookingCalculateBookingChildrenMessage(bookingId);
    }
}
