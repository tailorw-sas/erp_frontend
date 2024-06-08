package com.kynsof.identity.application.command.auth.registrySystemUser;

import com.kynsof.identity.domain.interfaces.service.IAuthService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrySystemUserCommandHandler implements ICommandHandler<RegistrySystemUserCommand> {
    private final IAuthService authService;

    public RegistrySystemUserCommandHandler(IAuthService authService) {

        this.authService = authService;
    }

    @Override
    public void handle(RegistrySystemUserCommand command) {
        String registerUser = authService.registerUserSystem(new UserSystemKycloackRequest(
                command.getUsername(), command.getEmail(),command.getFirstname(),
                command.getLastname(),command.getPassword(),command.getUserType()
        ), true);
        command.setId(UUID.fromString(registerUser));
    }
}
