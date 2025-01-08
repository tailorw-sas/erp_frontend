package com.tailorw.tcaInnsist.application.command.manageConnection.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageConnectionMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_TCA_CONFIGURATION_COMMAND";

    public DeleteManageConnectionMessage(UUID id){
        this.id = id;
    }
}
