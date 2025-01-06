package com.tailorw.tcaInnsist.application.command.manageConnection.createMany;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.tailorw.tcaInnsist.application.command.manageConnection.create.CreateManageConnectionCommand;
import com.tailorw.tcaInnsist.application.command.manageConnection.create.CreateManageConnectionMessage;
import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateManyManageConnectionCommand implements ICommand {

    List<CreateManageConnectionCommand> createManageConnectionCommands;

    public CreateManyManageConnectionCommand(List<CreateManageConnectionCommand> createManageConnectionCommands){
        this.createManageConnectionCommands = createManageConnectionCommands;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManyManageConnectionMessage();
    }
}
