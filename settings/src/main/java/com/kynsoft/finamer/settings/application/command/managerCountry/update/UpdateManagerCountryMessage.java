package com.kynsoft.finamer.settings.application.command.managerCountry.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagerCountryMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGER_COUNTY";

    public UpdateManagerCountryMessage(UUID id) {
        this.id = id;
    }

}
