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
    private Boolean isNightType;
    private String status;

    public CreateManageClientCommand(UUID id, String code, String name, Boolean isNightType, String status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.isNightType = isNightType;
        this.status = status;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageClientMessage(id);
    }
}
