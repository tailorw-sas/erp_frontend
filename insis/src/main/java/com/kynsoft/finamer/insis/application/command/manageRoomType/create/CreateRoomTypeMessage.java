package com.kynsoft.finamer.insis.application.command.manageRoomType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateRoomTypeMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "CREATE_MANAGE_ROOM_TYPE";

    public CreateRoomTypeMessage(UUID id){
        this.id = id;
    }
}
