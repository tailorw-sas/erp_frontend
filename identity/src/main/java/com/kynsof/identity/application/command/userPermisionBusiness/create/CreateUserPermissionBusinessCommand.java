package com.kynsof.identity.application.command.userPermisionBusiness.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserPermissionBusinessCommand implements ICommand {

    private UserPermissionBusinessRequest payload;

    public CreateUserPermissionBusinessCommand(UserPermissionBusinessRequest payload) {
        this.payload = payload;
    }

    public static CreateUserPermissionBusinessCommand fromRequest(UserPermissionBusinessRequest request) {
        return new CreateUserPermissionBusinessCommand(request);
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateUserPermissionBusinessMessage(true);
    }
}
