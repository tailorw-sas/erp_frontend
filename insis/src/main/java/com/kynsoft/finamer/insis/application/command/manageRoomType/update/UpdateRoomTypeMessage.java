package com.kynsoft.finamer.insis.application.command.manageRoomType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateRoomTypeMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "UPDATE_MANAGE_ROOM_TYPE";

    public UpdateRoomTypeMessage(UUID id){
        this.id = id;
    }
}
