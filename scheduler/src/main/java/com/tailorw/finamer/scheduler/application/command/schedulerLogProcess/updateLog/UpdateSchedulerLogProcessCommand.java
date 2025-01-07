package com.tailorw.finamer.scheduler.application.command.schedulerLogProcess.updateLog;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateSchedulerLogProcessCommand implements ICommand {

    private UUID id;
    private LocalDateTime completedAt;
    private String details;

    public UpdateSchedulerLogProcessCommand(UUID id, LocalDateTime completedAt, String details){
        this.id = id;
        this.completedAt = completedAt;
        this.details = details;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateSchedulerLogProcessMessage(id);
    }
}
