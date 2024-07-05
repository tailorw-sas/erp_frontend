package com.kynsoft.finamer.invoicing.application.command.manageAdjustment.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteAdjustmentMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_ADJUSTMENT";

    public DeleteAdjustmentMessage(UUID id) {
        this.id = id;
    }

}
