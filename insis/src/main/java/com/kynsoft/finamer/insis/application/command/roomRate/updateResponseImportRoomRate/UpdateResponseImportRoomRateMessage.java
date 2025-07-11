package com.kynsoft.finamer.insis.application.command.roomRate.updateResponseImportRoomRate;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateResponseImportRoomRateMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "UPDATE_RESPONSE_IMPORT_BOOKING_COMMAND";

    public UpdateResponseImportRoomRateMessage(UUID id){
        this.id = id;
    }
}
