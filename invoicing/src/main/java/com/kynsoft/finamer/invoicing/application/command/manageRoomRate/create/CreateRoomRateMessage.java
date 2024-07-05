package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateRoomRateMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_ROOMRATE";

    public CreateRoomRateMessage(UUID id) {
        this.id = id;
    }

}
