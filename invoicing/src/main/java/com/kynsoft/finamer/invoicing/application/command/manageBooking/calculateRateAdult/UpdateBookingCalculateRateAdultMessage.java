package com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateRateAdult;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBookingCalculateRateAdultMessage implements ICommandMessage {

    private final UUID id;

    public UpdateBookingCalculateRateAdultMessage(UUID id) {
        this.id = id;
    }

}
