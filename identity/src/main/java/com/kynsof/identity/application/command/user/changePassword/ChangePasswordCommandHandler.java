package com.kynsof.identity.application.command.user.changePassword;


import com.kynsof.identity.domain.interfaces.service.IAuthService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class ChangePasswordCommandHandler implements ICommandHandler<ChangePasswordCommand> {
    private final IAuthService authService;

    public ChangePasswordCommandHandler(IAuthService authService) {

        this.authService = authService;
    }

    @Override
    public void handle(ChangePasswordCommand command) {
        Boolean result = authService.changePassword(command.getId().toString(), command.getNewPassword());
        command.setResul(result);
    }
}
