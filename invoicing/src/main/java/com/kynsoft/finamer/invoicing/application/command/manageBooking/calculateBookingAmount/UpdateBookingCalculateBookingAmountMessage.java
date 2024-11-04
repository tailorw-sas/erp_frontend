package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingAmount;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBookingCalculateBookingAmountMessage implements ICommandMessage {

    private final UUID id;

    public UpdateBookingCalculateBookingAmountMessage(UUID id) {
        this.id = id;
    }

}
