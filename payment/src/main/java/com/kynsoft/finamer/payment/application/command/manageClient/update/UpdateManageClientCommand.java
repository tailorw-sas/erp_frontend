package com.kynsoft.finamer.payment.application.command.manageClient.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageClientCommand implements ICommand {

    private UUID id;
    private String name;

    public UpdateManageClientCommand(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageClientMessage(id);
    }
}
