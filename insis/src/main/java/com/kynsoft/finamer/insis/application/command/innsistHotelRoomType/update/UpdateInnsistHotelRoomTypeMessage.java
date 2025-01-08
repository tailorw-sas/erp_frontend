package com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.update;


import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateInnsistHotelRoomTypeMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_TRADING_COMPANY_HOTEL";

    public UpdateInnsistHotelRoomTypeMessage(UUID id){
        this.id = id;
    }
}
