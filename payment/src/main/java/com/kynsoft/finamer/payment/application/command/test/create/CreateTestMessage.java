package com.kynsoft.finamer.payment.application.command.test.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateTestMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_TEST";

    public CreateTestMessage(UUID id) {
        this.id = id;
    }

}
