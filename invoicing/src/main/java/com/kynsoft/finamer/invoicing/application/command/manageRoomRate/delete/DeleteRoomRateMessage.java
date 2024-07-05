package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteRoomRateMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_ROOMRATE";

    public DeleteRoomRateMessage(UUID id) {
        this.id = id;
    }

}
