package com.kynsoft.finamer.settings.application.command.managerTimeZone.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagerTimeZoneMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGER_TIME_ZONE";

    public CreateManagerTimeZoneMessage(UUID id) {
        this.id = id;
    }

}
