package com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateBusinessProcessSchedulerMessage implements ICommandMessage {

    private final UUID id;
    private String command = "UPDATE_BUSINESS_PROCESS_SCHEDULER_COMMAND";

    public UpdateBusinessProcessSchedulerMessage(UUID id){
        this.id = id;
    }
}
