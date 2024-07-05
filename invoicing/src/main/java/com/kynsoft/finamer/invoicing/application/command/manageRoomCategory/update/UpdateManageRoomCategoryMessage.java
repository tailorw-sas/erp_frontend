package com.kynsoft.finamer.invoicing.application.command.manageRoomCategory.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageRoomCategoryMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_AGENCY_TYPE";

    public UpdateManageRoomCategoryMessage(UUID id) {
        this.id = id;
    }
}
