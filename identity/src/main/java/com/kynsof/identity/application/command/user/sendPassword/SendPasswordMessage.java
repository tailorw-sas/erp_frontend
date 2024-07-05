package com.kynsof.identity.application.command.user.sendPassword;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class SendPasswordMessage implements ICommandMessage {

    private final Boolean result;
    private final String command = "CHANGE_PASSWORD";

    public SendPasswordMessage(Boolean result) {
        this.result = result;
    }

}
