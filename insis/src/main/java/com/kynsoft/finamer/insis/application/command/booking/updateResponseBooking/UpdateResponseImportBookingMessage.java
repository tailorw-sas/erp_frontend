package com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateResponseImportBookingMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_RESPONSE_IMPORT_BOOKING_COMMAND";

    public UpdateResponseImportBookingMessage(UUID id){
        this.id = id;
    }
}
