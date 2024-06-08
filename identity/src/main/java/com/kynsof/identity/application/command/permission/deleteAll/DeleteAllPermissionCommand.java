package com.kynsof.identity.application.command.permission.deleteAll;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DeleteAllPermissionCommand implements ICommand {

    private List<UUID> ids;

    @Override
    public ICommandMessage getMessage() {
        return new DeleteAllPermissionMessage();
    }

}
