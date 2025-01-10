package com.kynsoft.finamer.insis.application.command.manageRoomType.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateManyManageRoomTypeMessage implements ICommandMessage {
    private String command = "CREATE_MANY_ROOM_TYPE_COMMAND";

    public CreateManyManageRoomTypeMessage(){

    }
}
