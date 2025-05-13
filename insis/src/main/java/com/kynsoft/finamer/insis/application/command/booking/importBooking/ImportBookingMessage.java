package com.kynsoft.finamer.insis.application.command.booking.importBooking;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ImportBookingMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "IMPORT_BOOKINGS_COMMAND";

    public ImportBookingMessage(UUID id){
        this.id = id;
    }
}
