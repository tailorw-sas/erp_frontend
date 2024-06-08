package com.kynsoft.finamer.settings.application.command.manageAlerts.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateAlertMessage implements ICommandMessage {
    
    private final UUID id;

    private final String command = "UPDATE_ALERT";
    
    public UpdateAlertMessage(final UUID id) {this.id = id;}
}
