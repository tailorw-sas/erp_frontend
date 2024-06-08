package com.kynsoft.finamer.settings.application.command.manageRegion.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageRegionMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_REGION";

    public UpdateManageRegionMessage(UUID id) {
        this.id = id;
    }
}
