package com.kynsoft.finamer.insis.application.command.test.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateTestMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_TEST";

    public UpdateTestMessage(UUID id) {
        this.id = id;
    }

}
