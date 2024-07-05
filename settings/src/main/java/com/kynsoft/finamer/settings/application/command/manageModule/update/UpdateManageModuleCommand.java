package com.kynsoft.finamer.settings.application.command.manageModule.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageModuleCommand implements ICommand {

    private UUID id;
    private String name;
    private String status;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageModuleMessage(id);
    }
}
