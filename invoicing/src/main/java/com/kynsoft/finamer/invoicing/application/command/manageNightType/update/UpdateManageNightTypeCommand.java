package com.kynsoft.finamer.invoicing.application.command.manageNightType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageNightTypeCommand implements ICommand {

    private UUID id;
    private String name;


    public UpdateManageNightTypeCommand(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageNightTypeMessage(id);
    }
}
