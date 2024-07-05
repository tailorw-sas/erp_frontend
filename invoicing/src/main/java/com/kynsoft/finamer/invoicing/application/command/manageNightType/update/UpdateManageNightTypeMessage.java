package com.kynsoft.finamer.invoicing.application.command.manageNightType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageNightTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_NIGHT_TYPE";

    public UpdateManageNightTypeMessage(UUID id) {
        this.id = id;
    }

}
