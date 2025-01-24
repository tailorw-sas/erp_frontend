package com.kynsoft.finamer.insis.application.command.manageHotel.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateHotelMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "UPDATE_MANAGE_HOTEL";

    public UpdateHotelMessage(UUID id){
        this.id = id;
    }
}
