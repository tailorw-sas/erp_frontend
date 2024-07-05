package com.kynsoft.finamer.creditcard.application.command.manageHotel.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageHotelMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_HOTEL";

    public UpdateManageHotelMessage(UUID id) {
        this.id = id;
    }
}
