package com.kynsof.identity.application.command.auth.firstsChangePassword;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class FirstsChangePasswordMessage implements ICommandMessage {

    private final Boolean result;
    private final String command = "CHANGE_PASSWORD";

    public FirstsChangePasswordMessage(Boolean result) {
        this.result = result;
    }

}
