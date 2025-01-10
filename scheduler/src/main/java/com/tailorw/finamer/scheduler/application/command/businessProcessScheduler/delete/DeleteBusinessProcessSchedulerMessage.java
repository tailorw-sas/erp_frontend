package com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteBusinessProcessSchedulerMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "DELETE_BUSINESS_PROCESS_SCHEDULER_COMMAND";

    public DeleteBusinessProcessSchedulerMessage(UUID id){
        this.id = id;
    }
}
