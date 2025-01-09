package com.kynsoft.finamer.insis.application.command.manageRoomCategory.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageRoomCategoryMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_MANAGE_ROOM_CATEGORY_COMMAND";

    public CreateManageRoomCategoryMessage(UUID id){
        this.id = id;
    }
}
