package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingChildren;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBookingCalculateBookingChildrenMessage implements ICommandMessage {

    private final UUID id;

    public UpdateBookingCalculateBookingChildrenMessage(UUID id) {
        this.id = id;
    }

}
