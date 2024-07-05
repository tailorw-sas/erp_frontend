package com.kynsoft.finamer.invoicing.application.command.manageRoomCategory.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageRoomCategoryMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_AGENCY_TYPE";

    public CreateManageRoomCategoryMessage(UUID id) {
        this.id = id;
    }
}
