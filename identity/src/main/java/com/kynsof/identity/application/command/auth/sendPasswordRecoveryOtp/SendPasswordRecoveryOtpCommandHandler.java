package com.kynsof.identity.application.command.auth.sendPasswordRecoveryOtp;


import com.kynsof.identity.domain.interfaces.service.IAuthService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class SendPasswordRecoveryOtpCommandHandler implements ICommandHandler<SendPasswordRecoveryOtpCommand> {
    private final IAuthService authService;

    public SendPasswordRecoveryOtpCommandHandler(IAuthService authService) {

        this.authService = authService;
    }

    @Override
    public void handle(SendPasswordRecoveryOtpCommand command) {
        Boolean result = authService.sendPasswordRecoveryOtp(command.getEmail());
        command.setResul(result);
    }
}
