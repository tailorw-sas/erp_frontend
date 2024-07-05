package com.kynsoft.finamer.invoicing.application.command.manageAdjustment.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateAdjustmentMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_ADJUSTMENT";

    public UpdateAdjustmentMessage(UUID id) {
        this.id = id;
    }

}
