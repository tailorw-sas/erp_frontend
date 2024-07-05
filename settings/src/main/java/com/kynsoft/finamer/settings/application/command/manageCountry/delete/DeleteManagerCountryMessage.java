package com.kynsoft.finamer.settings.application.command.manageCountry.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManagerCountryMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGER_COUNTY";

    public DeleteManagerCountryMessage(UUID id) {
        this.id = id;
    }

}
