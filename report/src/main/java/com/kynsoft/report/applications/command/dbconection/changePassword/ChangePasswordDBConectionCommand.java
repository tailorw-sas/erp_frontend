package com.kynsoft.report.applications.command.dbconection.changePassword;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ChangePasswordDBConectionCommand implements ICommand {

    private final UUID id;
    private final String oldPassword;
    private final String newPassword;

    public static ChangePasswordDBConectionCommand fromRequest(UUID id, ChangePasswordDBConectionRequest request){
        return  new ChangePasswordDBConectionCommand(
                id, request.getOldPassword(), request.getNewPassword()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new ChangePasswordDBConectionMessage(id);
    }
}
