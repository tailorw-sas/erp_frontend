package com.kynsoft.finamer.settings.application.command.manageModule.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageModuleCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String status;
    private String image;
    private String description;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageModuleMessage(id);
    }
}
