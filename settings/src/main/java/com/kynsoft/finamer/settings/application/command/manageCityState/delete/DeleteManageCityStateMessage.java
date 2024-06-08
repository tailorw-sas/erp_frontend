package com.kynsoft.finamer.settings.application.command.manageCityState.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageCityStateMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_CITY_STATE";

    public DeleteManageCityStateMessage(UUID id) {
        this.id = id;
    }

}
