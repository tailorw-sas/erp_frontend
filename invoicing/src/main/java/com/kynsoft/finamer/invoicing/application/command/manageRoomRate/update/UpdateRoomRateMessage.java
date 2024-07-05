package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateRoomRateMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_ROOMRATE";

    public UpdateRoomRateMessage(UUID id) {
        this.id = id;
    }

}
