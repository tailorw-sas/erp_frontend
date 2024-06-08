package com.kynsof.identity.application.command.userPermisionBusiness.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteUserRolBusinessMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_USER_ROL_BUSINESS";

    public DeleteUserRolBusinessMessage(UUID id) {
        this.id = id;
    }

}
