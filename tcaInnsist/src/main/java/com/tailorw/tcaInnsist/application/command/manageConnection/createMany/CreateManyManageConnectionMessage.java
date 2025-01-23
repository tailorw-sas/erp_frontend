package com.tailorw.tcaInnsist.application.command.manageConnection.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

@Getter
public class CreateManyManageConnectionMessage implements ICommandMessage {
    private final String command = "CREATE_MANY_MANAGE_CONNECTION";

    public CreateManyManageConnectionMessage(){}
}
