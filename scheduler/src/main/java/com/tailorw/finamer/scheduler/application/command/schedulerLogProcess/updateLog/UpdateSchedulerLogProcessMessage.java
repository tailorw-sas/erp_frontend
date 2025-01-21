package com.tailorw.finamer.scheduler.application.command.schedulerLogProcess.updateLog;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateSchedulerLogProcessMessage implements ICommandMessage {
    private final UUID id;
    private final String command = "UPDATE_SCHEDULER_LOG_PROCESS_COMMAND";

    public UpdateSchedulerLogProcessMessage(UUID id){
        this.id = id;
    }
}
