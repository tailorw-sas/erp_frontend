package com.kynsof.identity.application.command.user.delete;



import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteUserSystemsMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_USER";

    public DeleteUserSystemsMessage(UUID id) {
        this.id = id;
    }

}
