package com.kynsoft.finamer.settings.application.command.managerTimeZone.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagerTimeZoneMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGER_TIME_ZONE";

    public UpdateManagerTimeZoneMessage(UUID id) {
        this.id = id;
    }
}
