package com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateInnsistHotelRoomTypeMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_HOTEL_ROOM_TYPE_COMMAND";

    public CreateInnsistHotelRoomTypeMessage(UUID id){
        this.id = id;
    }
}
