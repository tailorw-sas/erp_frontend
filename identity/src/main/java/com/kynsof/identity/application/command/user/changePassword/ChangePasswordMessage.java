package com.kynsof.identity.application.command.user.changePassword;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class ChangePasswordMessage implements ICommandMessage {

    private final Boolean result;
    private final String command = "CHANGE_PASSWORD";

    public ChangePasswordMessage(Boolean result) {
        this.result = result;
    }

}
