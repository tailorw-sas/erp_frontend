package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateChickInAndCheckOut;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBookingCalculateCheckInAndCheckOutMessage implements ICommandMessage {

    private final UUID id;

    public UpdateBookingCalculateCheckInAndCheckOutMessage(UUID id) {
        this.id = id;
    }

}
