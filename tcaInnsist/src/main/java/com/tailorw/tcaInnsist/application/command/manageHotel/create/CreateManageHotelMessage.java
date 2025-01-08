package com.tailorw.tcaInnsist.application.command.manageHotel.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageHotelMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_MANAGE_HOTEL_COMMAND";

    public CreateManageHotelMessage(UUID id){
        this.id = id;
    }
}
