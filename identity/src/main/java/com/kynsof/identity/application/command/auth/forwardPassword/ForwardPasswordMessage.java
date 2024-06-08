package com.kynsof.identity.application.command.auth.forwardPassword;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class ForwardPasswordMessage implements ICommandMessage {

    private final Boolean result;
    private final String command = "FORWARD_PASSWORD";

    public ForwardPasswordMessage(Boolean result) {
        this.result = result;
    }

}
