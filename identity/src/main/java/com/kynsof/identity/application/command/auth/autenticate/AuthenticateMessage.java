package com.kynsof.identity.application.command.auth.autenticate;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class AuthenticateMessage implements ICommandMessage {

    private final TokenResponse tokenResponse;
    private final String command = "AUTHENTICATE";

    public AuthenticateMessage(TokenResponse token) {
        tokenResponse = token;
    }

}
