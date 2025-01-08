package com.tailorw.tcaInnsist.application.command.manageHotel.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateManyManageHotelMessage implements ICommandMessage {
    private final String command = "CREATE_MANY_MANAGE_HOTEL_COMMAND";

    public CreateManyManageHotelMessage(){}
}
