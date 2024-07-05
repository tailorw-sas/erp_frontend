package com.kynsoft.finamer.invoicing.application.command.manageNightType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageNightTypeMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_NIGHT_TYPE";

    public DeleteManageNightTypeMessage(UUID id) {
        this.id = id;
    }
}
