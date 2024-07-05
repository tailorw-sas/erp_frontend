package com.kynsoft.finamer.settings.application.command.manageCityState.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageCityStateMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_AGENCY";

    public CreateManageCityStateMessage(UUID id) {
        this.id = id;
    }

}
