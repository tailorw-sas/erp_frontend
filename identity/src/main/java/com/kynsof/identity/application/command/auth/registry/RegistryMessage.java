package com.kynsof.identity.application.command.auth.registry;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class RegistryMessage implements ICommandMessage {

    private final String result;
    private final String command = "REGISTRY";

    public RegistryMessage(String result) {
        this.result = result;
    }

}
