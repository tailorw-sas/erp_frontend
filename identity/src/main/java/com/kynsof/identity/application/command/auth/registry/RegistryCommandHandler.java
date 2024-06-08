package com.kynsof.identity.application.command.auth.registry;


import com.kynsof.identity.domain.interfaces.service.IAuthService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class RegistryCommandHandler implements ICommandHandler<RegistryCommand> {
    private final IAuthService authService;

    public RegistryCommandHandler(IAuthService authService) {

        this.authService = authService;
    }

    @Override
    public void handle(RegistryCommand command) {
        String registerUser = authService.registerUser(new UserRequest(
                command.getUsername(), command.getEmail(),command.getFirstname(),
                command.getLastname(),command.getPassword()
        ), false);
        command.setResul(registerUser);
    }
}
