package com.kynsoft.finamer.settings.application.command.manageRoomCategory.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageRoomCategoryMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_AGENCY_TYPE";

    public DeleteManageRoomCategoryMessage(UUID id) {
        this.id = id;
    }
}
