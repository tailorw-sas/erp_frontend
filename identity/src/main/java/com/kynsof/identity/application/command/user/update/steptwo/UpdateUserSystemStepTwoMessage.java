package com.kynsof.identity.application.command.user.update.steptwo;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateUserSystemStepTwoMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "UPDATE_USER_SYSTEM";

    public UpdateUserSystemStepTwoMessage(UUID id) {
        this.id = id;
    }

}
