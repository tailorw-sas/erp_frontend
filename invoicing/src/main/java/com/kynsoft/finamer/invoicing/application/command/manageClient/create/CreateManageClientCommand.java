package com.kynsoft.finamer.invoicing.application.command.manageClient.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageClientCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;

    public CreateManageClientCommand(UUID id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;

    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageClientMessage(id);
    }
}
