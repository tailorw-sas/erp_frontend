package com.kynsoft.finamer.settings.application.command.manageCityState.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageCityStateMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_AGENCY";

    public UpdateManageCityStateMessage(UUID id) {
        this.id = id;
    }

}
