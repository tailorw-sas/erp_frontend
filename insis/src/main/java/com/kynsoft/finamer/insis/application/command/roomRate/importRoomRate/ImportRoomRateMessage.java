package com.kynsoft.finamer.insis.application.command.roomRate.importRoomRate;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ImportRoomRateMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "IMPORT_BOOKINGS_COMMAND";

    public ImportRoomRateMessage(UUID id){
        this.id = id;
    }
}
