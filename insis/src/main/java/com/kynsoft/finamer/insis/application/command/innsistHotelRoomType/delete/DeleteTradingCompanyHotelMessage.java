package com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteTradingCompanyHotelMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_TRADING_COMPANY_HOTEL_COMMAND";

    public DeleteTradingCompanyHotelMessage(UUID id){
        this.id = id;
    }
}
