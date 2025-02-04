package com.tailorw.finamer.scheduler.application.command.businessProcess.delete;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteBusinessProcessCommand implements ICommand {

    private UUID id;

    public DeleteBusinessProcessCommand(UUID id){
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteBusinessProcessMessage(id);
    }
}
