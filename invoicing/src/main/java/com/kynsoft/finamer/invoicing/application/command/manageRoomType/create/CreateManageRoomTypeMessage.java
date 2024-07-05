package com.kynsoft.finamer.invoicing.application.command.manageRoomType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageRoomTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_ROOM_TYPE";

    public CreateManageRoomTypeMessage(UUID id) {
        this.id = id;
    }
}
