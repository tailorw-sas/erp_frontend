package com.kynsof.identity.application.command.auth.autenticate;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticateCommand implements ICommand {
    private TokenResponse tokenResponse;
    private String userName;
    private String password;



    public AuthenticateCommand( String userName, String password) {
        this.userName = userName;
        this.password = password;
    }



    @Override
    public ICommandMessage getMessage() {
        return new AuthenticateMessage(tokenResponse);
    }
}
