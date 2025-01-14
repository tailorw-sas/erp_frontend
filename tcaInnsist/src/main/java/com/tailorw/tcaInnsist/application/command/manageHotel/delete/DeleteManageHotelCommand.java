package com.tailorw.tcaInnsist.application.command.manageHotel.delete;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageHotelCommand implements ICommand {

    private UUID id;

    public DeleteManageHotelCommand(UUID id){
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteManageHotelMessage();
    }
}
