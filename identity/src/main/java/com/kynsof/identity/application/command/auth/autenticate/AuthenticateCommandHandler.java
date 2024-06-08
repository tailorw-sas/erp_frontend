package com.kynsof.identity.application.command.auth.autenticate;


import com.kynsof.identity.domain.interfaces.service.IAuthService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class AuthenticateCommandHandler implements ICommandHandler<AuthenticateCommand> {
    private final IAuthService authService;

    public AuthenticateCommandHandler(IAuthService authService) {

        this.authService = authService;
    }

    @Override
    public void handle(AuthenticateCommand command) {
        TokenResponse tokenResponse = authService.authenticate(new LoginRequest(command.getUserName(), command.getPassword()));
        command.setTokenResponse(tokenResponse);
    }
}
