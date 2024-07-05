package com.kynsoft.finamer.settings.application.command.manageCountry.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagerCountryMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGER_COUNTY";

    public CreateManagerCountryMessage(UUID id) {
        this.id = id;
    }

}
