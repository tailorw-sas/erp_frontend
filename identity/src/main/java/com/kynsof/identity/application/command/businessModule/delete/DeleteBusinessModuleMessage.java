package com.kynsof.identity.application.command.businessModule.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteBusinessModuleMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_BUSINESS_MODULE";

    public DeleteBusinessModuleMessage(UUID id) {
        this.id = id;
    }

}
