package com.tailorw.tcaInnsist.application.command.manageConnection.delete;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageConnectionCommand implements ICommand {

    private UUID id;

    public DeleteManageConnectionCommand(UUID id){
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteManageConnectionMessage(id);
    }
}
