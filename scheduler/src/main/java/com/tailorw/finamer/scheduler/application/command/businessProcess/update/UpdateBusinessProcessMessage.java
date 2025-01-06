package com.tailorw.finamer.scheduler.application.command.businessProcess.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBusinessProcessMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "UPDATE_BUSINESS_PROCESS_COMMAND";

    public UpdateBusinessProcessMessage(UUID id){
        this.id = id;
    }
}
