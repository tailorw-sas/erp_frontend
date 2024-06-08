package com.kynsof.identity.application.command.auth.forwardPassword;


import com.kynsof.identity.domain.interfaces.service.IAuthService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class ForwardPasswordCommandHandler implements ICommandHandler<ForwardPasswordCommand> {
    private final IAuthService authService;

    public ForwardPasswordCommandHandler(IAuthService authService) {

        this.authService = authService;
    }

    @Override
    public void handle(ForwardPasswordCommand command) {
        Boolean result = authService.forwardPassword(new PasswordChangeRequest(command.getEmail(),
                command.getNewPassword(), command.getOtp()));
        command.setResul(result);
    }
}
