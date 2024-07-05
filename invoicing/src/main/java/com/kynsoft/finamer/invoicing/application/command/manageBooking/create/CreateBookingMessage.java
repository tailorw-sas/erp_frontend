package com.kynsoft.finamer.invoicing.application.command.manageBooking.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateBookingMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_BOOKING";

    public CreateBookingMessage(UUID id) {
        this.id = id;
    }

}
