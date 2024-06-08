package com.kynsoft.finamer.settings.application.command.manageHotel.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageHotelMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "DELETE_MANAGE_HOTEL";

    public DeleteManageHotelMessage(UUID id) {
        this.id = id;
    }
}
