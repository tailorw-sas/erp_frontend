package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateRateChild;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBookingCalculateRateChildMessage implements ICommandMessage {

    private final UUID id;

    public UpdateBookingCalculateRateChildMessage(UUID id) {
        this.id = id;
    }

}
