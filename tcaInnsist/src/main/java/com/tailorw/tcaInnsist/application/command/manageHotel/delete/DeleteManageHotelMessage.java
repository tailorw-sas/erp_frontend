package com.tailorw.tcaInnsist.application.command.manageHotel.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class DeleteManageHotelMessage implements ICommandMessage {
    private final String command = "DELETE_MANAGE_HOTEL_COMMAND";

    public DeleteManageHotelMessage(){}
}
