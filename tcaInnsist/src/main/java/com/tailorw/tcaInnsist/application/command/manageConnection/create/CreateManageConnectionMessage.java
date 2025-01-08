package com.tailorw.tcaInnsist.application.command.manageConnection.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageConnectionMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "CREATE_MANAGE_CONNECTION_COMMAND";

    public CreateManageConnectionMessage(UUID id){
        this.id = id;
    }
}
