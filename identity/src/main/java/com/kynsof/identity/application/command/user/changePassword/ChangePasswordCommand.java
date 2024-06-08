package com.kynsof.identity.application.command.user.changePassword;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChangePasswordCommand implements ICommand {
    private Boolean resul;
    private final UUID id;
    private  final String newPassword;

    public ChangePasswordCommand(UUID id, String newPassword) {
        this.id = id;
        this.newPassword = newPassword;
    }

    public static ChangePasswordCommand fromRequest(UUID id, ChangePasswordRequest request) {
        return new ChangePasswordCommand(id, request.getNewPassword());
    }


    @Override
    public ICommandMessage getMessage() {
        return new ChangePasswordMessage(resul);
    }
}
