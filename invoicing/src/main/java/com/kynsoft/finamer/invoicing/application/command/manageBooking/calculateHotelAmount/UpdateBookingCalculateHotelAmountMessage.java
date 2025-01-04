package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateHotelAmount;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBookingCalculateHotelAmountMessage implements ICommandMessage {

    private final UUID id;

    public UpdateBookingCalculateHotelAmountMessage(UUID id) {
        this.id = id;
    }

}
