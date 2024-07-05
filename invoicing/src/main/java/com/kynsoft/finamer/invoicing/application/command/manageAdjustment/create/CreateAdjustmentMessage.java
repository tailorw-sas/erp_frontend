package com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateAdjustmentMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_ADJUSTMENT";

    public CreateAdjustmentMessage(UUID id) {
        this.id = id;
    }

}
