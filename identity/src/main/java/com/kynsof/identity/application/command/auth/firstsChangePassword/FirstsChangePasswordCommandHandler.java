package com.kynsof.identity.application.command.auth.firstsChangePassword;


import com.kynsof.identity.domain.dto.UserSystemDto;
import com.kynsof.identity.domain.interfaces.service.IAuthService;
import com.kynsof.identity.domain.interfaces.service.IUserSystemService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.UserNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class FirstsChangePasswordCommandHandler implements ICommandHandler<FirstsChangePasswordCommand> {
    private final IAuthService authService;
    private final IUserSystemService userSystemService;

    public FirstsChangePasswordCommandHandler(IAuthService authService, IUserSystemService userSystemService) {

        this.authService = authService;
        this.userSystemService = userSystemService;
    }

    @Override
    public void handle(FirstsChangePasswordCommand command) {

        UserSystemDto userSystemDto = userSystemService.findByEmail(command.getEmail());
        if (userSystemDto != null) {
            Boolean result = authService.firstChangePassword(userSystemDto.getId().toString(), command.getEmail(),
                    command.getNewPassword(), command.getOldPassword());
            command.setResul(result);
        }

    }
}
