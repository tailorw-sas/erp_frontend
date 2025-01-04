package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingAdults;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBookingCalculateBookingAdultsMessage implements ICommandMessage {

    private final UUID id;

    public UpdateBookingCalculateBookingAdultsMessage(UUID id) {
        this.id = id;
    }

}
