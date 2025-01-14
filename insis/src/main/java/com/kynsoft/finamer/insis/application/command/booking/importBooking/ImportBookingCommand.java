package com.kynsoft.finamer.insis.application.command.booking.importBooking;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ImportBookingCommand implements ICommand {

    public UUID id;
    public UUID userId;
    public List<UUID> bookings;

    public ImportBookingCommand(UUID id, UUID userId, List<UUID> bookings){
        this.id = id;
        this.userId = userId;
        this.bookings = bookings;
    }

    public static ImportBookingCommand fromRequest(ImportBookingRequest request){
        return new ImportBookingCommand(request.id,
                request.getUserId(),
                request.getBookings());
    }

    @Override
    public ICommandMessage getMessage() {
        return new ImportBookingMessage(id);
    }
}
