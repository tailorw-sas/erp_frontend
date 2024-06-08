package com.kynsof.identity.application.command.user.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class UpdateUserSystemMessage implements ICommandMessage {


    private final String command = "UPDATE_USER_SYSTEM";

    public UpdateUserSystemMessage() {

    }

}
