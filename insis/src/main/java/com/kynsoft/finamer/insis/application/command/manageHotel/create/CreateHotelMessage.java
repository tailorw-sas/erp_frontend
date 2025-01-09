package com.kynsoft.finamer.insis.application.command.manageHotel.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateHotelMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_HOTEL";

    public CreateHotelMessage(UUID id) {
        this.id = id;
    }
}
