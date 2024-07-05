package com.kynsof.identity.application.command.auth.firstsChangePassword;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirstsChangePasswordCommand implements ICommand {
    private Boolean resul;
    private String email;
    private String newPassword;
    private String oldPassword;



    public FirstsChangePasswordCommand(String email, String newPassword, String oldPassword) {

        this.email = email;
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
    }

    public static FirstsChangePasswordCommand fromRequest(FirstsChangePasswordRequest request) {
        return new FirstsChangePasswordCommand(request.getEmail(), request.getNewPassword(), request.getOldPassword());
    }


    @Override
    public ICommandMessage getMessage() {
        return new FirstsChangePasswordMessage(resul);
    }
}
