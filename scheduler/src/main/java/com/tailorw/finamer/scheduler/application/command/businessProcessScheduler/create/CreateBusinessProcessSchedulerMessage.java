package com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateBusinessProcessSchedulerMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "CREATE_BUSINESS_PROCESS_SCHEDULER_COMMAND";

    public CreateBusinessProcessSchedulerMessage(UUID id){
        this.id = id;
    }
}
