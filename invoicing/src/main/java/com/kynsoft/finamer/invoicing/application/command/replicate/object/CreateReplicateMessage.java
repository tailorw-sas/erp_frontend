package com.kynsoft.finamer.invoicing.application.command.replicate.object;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateReplicateMessage implements ICommandMessage {

    private final String command = "TO_REPLICATE";

    public CreateReplicateMessage() {
    }

}
