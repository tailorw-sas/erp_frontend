package com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.delete;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteTradingCompanyHotelCommand implements ICommand {

    private UUID id;

    public DeleteTradingCompanyHotelCommand(UUID id){
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteTradingCompanyHotelMessage(id);
    }
}
