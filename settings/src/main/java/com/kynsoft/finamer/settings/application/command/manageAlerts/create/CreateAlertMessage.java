package com.kynsoft.finamer.settings.application.command.manageAlerts.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateAlertMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_ALERT";
    
    public CreateAlertMessage(final UUID id) {this.id = id;}
}
