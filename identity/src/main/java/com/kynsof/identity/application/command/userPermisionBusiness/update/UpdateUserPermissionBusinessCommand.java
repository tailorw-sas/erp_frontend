package com.kynsof.identity.application.command.userPermisionBusiness.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserPermissionBusinessCommand implements ICommand {

    private UserRoleBusinessUpdateRequest payload;

    public UpdateUserPermissionBusinessCommand(UserRoleBusinessUpdateRequest payload) {
        this.payload = payload;
    }

    public static UpdateUserPermissionBusinessCommand fromRequest(UserRoleBusinessUpdateRequest request) {
        return new UpdateUserPermissionBusinessCommand(request);
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateUserPermissionBusinessMessage(true);
    }
}
