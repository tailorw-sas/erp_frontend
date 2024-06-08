package com.kynsof.identity.application.command.business.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBusinessMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_BUSINESS";

    public UpdateBusinessMessage(UUID id) {
        this.id = id;
    }

}
