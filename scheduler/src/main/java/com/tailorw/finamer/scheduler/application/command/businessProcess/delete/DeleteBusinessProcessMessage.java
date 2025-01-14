package com.tailorw.finamer.scheduler.application.command.businessProcess.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteBusinessProcessMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "DELETE_BUSINESS_PROCESS_COMMAND";

    public DeleteBusinessProcessMessage(UUID id){
        this.id = id;
    }
}
