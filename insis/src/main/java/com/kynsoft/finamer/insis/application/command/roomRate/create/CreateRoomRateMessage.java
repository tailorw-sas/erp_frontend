package com.kynsoft.finamer.insis.application.command.roomRate.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateRoomRateMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_RATE_COMMAND";

    public CreateRoomRateMessage(UUID id){
        this.id = id;
    }
}
