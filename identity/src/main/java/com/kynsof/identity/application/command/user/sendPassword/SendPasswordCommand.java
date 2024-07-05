package com.kynsof.identity.application.command.user.sendPassword;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SendPasswordCommand implements ICommand {
    private Boolean resul;
    private final UUID id;
    private  final String newPassword;

    public SendPasswordCommand(UUID id, String newPassword) {
        this.id = id;
        this.newPassword = newPassword;
    }

    public static SendPasswordCommand fromRequest( SendPasswordRequest request) {
        return new SendPasswordCommand(request.getUserId(), request.getNewPassword());
    }


    @Override
    public ICommandMessage getMessage() {
        return new SendPasswordMessage(resul);
    }
}
