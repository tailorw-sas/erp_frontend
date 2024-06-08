package com.kynsoft.finamer.settings.application.command.manageAlerts.delete;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DeleteAlertCommand implements ICommand {

    private UUID id;
    
    @Override
    public ICommandMessage getMessage() {
        return new DeleteAlertMessage(id);
    }
}
