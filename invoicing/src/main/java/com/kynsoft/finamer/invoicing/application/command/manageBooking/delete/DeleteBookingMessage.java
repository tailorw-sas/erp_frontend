package com.kynsoft.finamer.invoicing.application.command.manageBooking.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteBookingMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_BOOKING";

    public DeleteBookingMessage(UUID id) {
        this.id = id;
    }

}
