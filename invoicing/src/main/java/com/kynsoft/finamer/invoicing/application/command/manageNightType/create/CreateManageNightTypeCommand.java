package com.kynsoft.finamer.invoicing.application.command.manageNightType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageNightTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;

    public CreateManageNightTypeCommand(UUID id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageNightTypeMessage(id);
    }
}
