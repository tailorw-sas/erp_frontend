package com.kynsoft.finamer.invoicing.application.command.manageClient.update;

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
    private Boolean isNightType;
    private String status;

    public UpdateManageClientCommand(UUID id, String name, Boolean isNightType, String status) {
        this.id = id;
        this.name = name;
        this.isNightType = isNightType;
        this.status = status;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageClientMessage(id);
    }
}
