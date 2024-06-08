package com.kynsof.identity.application.command.module.deleteAll;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class DeleteAllModuleMessage implements ICommandMessage {

    private final String command = "DELETE";

    public DeleteAllModuleMessage() {}

}
