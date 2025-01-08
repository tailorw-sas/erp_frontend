package com.kynsoft.finamer.insis.application.command.manageAgency.delete;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteAgencyCommand implements ICommand {
    private final UUID id;

    public DeleteAgencyCommand(UUID id){
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        return new DeleteAgencyMessage(id);
    }
}
