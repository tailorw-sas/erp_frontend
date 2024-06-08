package com.kynsof.identity.application.command.auth.registrySystemUser;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class RegistrySystemUserMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "REGISTRY_SYSTEM_USER";

    public RegistrySystemUserMessage(UUID result) {
        this.id = result;
    }

}
