package com.kynsoft.finamer.insis.application.command.booking.undoImportBooking;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UndoImportBookingMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UNDO_IMPORT_COMMAND";

    public UndoImportBookingMessage(UUID id){
        this.id = id;
    }
}
