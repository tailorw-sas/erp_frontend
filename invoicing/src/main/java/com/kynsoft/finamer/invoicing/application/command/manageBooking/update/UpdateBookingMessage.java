package com.kynsoft.finamer.invoicing.application.command.manageBooking.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBookingMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_BOOKING";

    public UpdateBookingMessage(UUID id) {
        this.id = id;
    }

}
