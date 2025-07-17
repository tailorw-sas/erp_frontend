package com.kynsoft.finamer.insis.application.command.roomRate.undoImportRoomRate;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UndoImportRoomRateMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UNDO_IMPORT_COMMAND";

    public UndoImportRoomRateMessage(UUID id){
        this.id = id;
    }
}
