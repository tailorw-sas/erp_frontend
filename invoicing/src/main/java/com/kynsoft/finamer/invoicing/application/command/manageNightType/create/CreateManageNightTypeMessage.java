package com.kynsoft.finamer.invoicing.application.command.manageNightType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageNightTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_NIGHT_TYPE";

    public CreateManageNightTypeMessage(UUID id) {
        this.id = id;
    }

}
