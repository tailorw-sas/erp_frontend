package com.kynsof.identity.application.command.userPermisionBusiness.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class UpdateUserPermissionBusinessMessage implements ICommandMessage {

    private final boolean result;

    private final String command = "UPDATE_USER_PERMISSION_BUSINESS";

    public UpdateUserPermissionBusinessMessage(boolean result) {
        this.result = result;
    }

}
