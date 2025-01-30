package com.tailorw.tcaInnsist.application.command.manageHotel.createMany;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.tailorw.tcaInnsist.application.command.manageHotel.create.CreateManageHotelCommand;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateManyManageHotelCommand implements ICommand {

    private List<CreateManageHotelCommand> createManageHotelCommands;

    public CreateManyManageHotelCommand(List<CreateManageHotelCommand> createManageHotelCommands){
        this.createManageHotelCommands = createManageHotelCommands;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManyManageHotelMessage();
    }
}
