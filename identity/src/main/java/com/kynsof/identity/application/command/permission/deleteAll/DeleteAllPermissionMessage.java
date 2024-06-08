package com.kynsof.identity.application.command.permission.deleteAll;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class DeleteAllPermissionMessage implements ICommandMessage {

    private final String command = "DELETE";

    public DeleteAllPermissionMessage() {}

}
