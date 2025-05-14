package com.kynsoft.finamer.insis.application.command.booking.undoImportBooking;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UndoImportBookingCommand implements ICommand {

    private UUID bookingId;

    public UndoImportBookingCommand(UUID bookingId){
        this.bookingId = bookingId;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UndoImportBookingMessage(bookingId);
    }
}
