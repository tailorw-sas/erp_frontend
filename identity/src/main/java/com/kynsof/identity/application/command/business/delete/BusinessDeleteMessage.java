package com.kynsof.identity.application.command.business.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BusinessDeleteMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_BUSINESS";

    public BusinessDeleteMessage(UUID id) {
        this.id = id;
    }

}
