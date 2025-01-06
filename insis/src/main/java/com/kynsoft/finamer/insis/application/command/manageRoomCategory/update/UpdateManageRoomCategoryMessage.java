package com.kynsoft.finamer.insis.application.command.manageRoomCategory.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageRoomCategoryMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "UPDATE_MANAGE_ROOM_CATEGORY_COMMAND";

    public UpdateManageRoomCategoryMessage(UUID id){
        this.id = id;
    }
}
