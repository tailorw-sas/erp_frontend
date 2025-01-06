package com.kynsoft.finamer.insis.application.command.manageRoomCategory.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateManyManageRoomCategoryMessage implements ICommandMessage {

    private final String command = "CREATE_MANY_MANAGE_ROOM_CATEGORY_COMMAND";

    public CreateManyManageRoomCategoryMessage(){}
}
