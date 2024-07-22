package com.kynsoft.finamer.settings.application.command.replicate.objects;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateReplicateMessage implements ICommandMessage {

    private final String command = "TO_REPLICATE";

    public CreateReplicateMessage() {
    }

}
