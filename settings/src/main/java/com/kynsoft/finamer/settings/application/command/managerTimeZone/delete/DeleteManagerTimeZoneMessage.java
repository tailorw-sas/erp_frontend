package com.kynsoft.finamer.settings.application.command.managerTimeZone.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManagerTimeZoneMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGER_TIME_ZONE";

    public DeleteManagerTimeZoneMessage(UUID id) {
        this.id = id;
    }

}
