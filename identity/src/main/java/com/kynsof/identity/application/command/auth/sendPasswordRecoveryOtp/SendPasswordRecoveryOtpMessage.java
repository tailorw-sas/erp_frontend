package com.kynsof.identity.application.command.auth.sendPasswordRecoveryOtp;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class SendPasswordRecoveryOtpMessage implements ICommandMessage {

    private final Boolean result;
    private final String command = "REGISTRY";

    public SendPasswordRecoveryOtpMessage(Boolean result) {
        this.result = result;
    }

}
