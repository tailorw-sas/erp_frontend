package com.kynsoft.finamer.invoicing.application.command.manageRoomType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageRoomTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_ROOM_TYPE";

    public UpdateManageRoomTypeMessage(UUID id) {
        this.id = id;
    }
}
