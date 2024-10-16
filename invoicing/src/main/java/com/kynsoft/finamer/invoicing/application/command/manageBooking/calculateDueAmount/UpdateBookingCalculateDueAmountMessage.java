package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateDueAmount;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBookingCalculateDueAmountMessage implements ICommandMessage {

    private final UUID id;

    public UpdateBookingCalculateDueAmountMessage(UUID id) {
        this.id = id;
    }

}
