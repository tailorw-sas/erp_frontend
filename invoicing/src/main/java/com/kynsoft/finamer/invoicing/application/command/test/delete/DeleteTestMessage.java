package com.kynsoft.finamer.invoicing.application.command.test.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteTestMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_TEST";

    public DeleteTestMessage(UUID id) {
        this.id = id;
    }

}
