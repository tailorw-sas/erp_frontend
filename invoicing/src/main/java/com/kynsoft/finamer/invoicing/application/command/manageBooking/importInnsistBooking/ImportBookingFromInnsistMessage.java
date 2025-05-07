package com.kynsoft.finamer.invoicing.application.command.manageBooking.importInnsistBooking;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ImportBookingFromInnsistMessage implements ICommandMessage {

    private final UUID id;
    private String command = "IMPORT_BOOKING_FROM_INNSIST_COMMAND";

    public ImportBookingFromInnsistMessage(UUID id){
        this.id = id;
    }
}
