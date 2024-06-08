package com.kynsof.identity.application.command.business.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateBusinessMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_BUSINESS";

    public CreateBusinessMessage(UUID id) {
        this.id = id;
    }

}
