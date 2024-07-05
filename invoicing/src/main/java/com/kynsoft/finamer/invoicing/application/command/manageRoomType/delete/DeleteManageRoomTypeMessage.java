package com.kynsoft.finamer.invoicing.application.command.manageRoomType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageRoomTypeMessage implements ICommandMessage {

    private UUID id;

    private final String command = "DELETE_MANAGE_ROOM_TYPE";

    public DeleteManageRoomTypeMessage(UUID id) {
        this.id = id;
    }
}
