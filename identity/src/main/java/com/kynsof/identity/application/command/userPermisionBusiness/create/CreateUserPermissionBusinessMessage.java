package com.kynsof.identity.application.command.userPermisionBusiness.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateUserPermissionBusinessMessage implements ICommandMessage {

    private final boolean id;

    private final String command = "CREATE_USER_ROLE_BUSINESS";

    public CreateUserPermissionBusinessMessage(boolean id) {
        this.id = id;
    }

}
