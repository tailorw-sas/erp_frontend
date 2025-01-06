package com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateBusinessProcessSchedulerCommand implements ICommand {

    private UUID id;
    private UUID frequency;
    private UUID intervalType;
    private Integer interval;
    private UUID executionDateType;
    private String executionDateValue;
    private String executionDate;
    private String executionTime;
    private UUID processingDateType;
    private Integer processingDateValue;
    private String processingDate;
    private String params;
    private String status;
    private UUID process;
    private boolean allowsQueueing;


    public UpdateBusinessProcessSchedulerCommand(UUID id,
                                                 UUID frequency,
                                                 UUID intervalType,
                                                 Integer interval,
                                                 UUID executionDateType,
                                                 String executionDateValue,
                                                 String executionDate,
                                                 String executionTime,
                                                 UUID processingDateType,
                                                 Integer processingDateValue,
                                                 String processingDate,
                                                 String params,
                                                 String status,
                                                 UUID process,
                                                 boolean allowsQueueing){
        this.id = id;
        this.frequency = frequency;
        this.intervalType = intervalType;
        this.interval = interval;
        this.executionDateType = executionDateType;
        this.executionDateValue = executionDateValue;
        this.executionDate = executionDate;
        this.executionTime = executionTime;
        this.processingDateType = processingDateType;
        this.processingDateValue = processingDateValue;
        this.processingDate = processingDate;
        this.params = params;
        this.status = status;
        this.process = process;
        this.allowsQueueing = allowsQueueing;
    }

    public static UpdateBusinessProcessSchedulerCommand fromRequest(UUID id, UpdateBusinessProcessSchedulerRequest request){
        return new UpdateBusinessProcessSchedulerCommand(id,
                request.getFrequency(),
                request.getIntervalType(),
                request.getInterval(),
                request.getExecutionDateType(),
                request.getExecutionDateValue(),
                request.getExecutionDate(),
                request.getExecutionTime(),
                request.getProcessingDateType(),
                request.getProcessingDateValue(),
                request.getProcessingDate(),
                request.getParams(),
                request.getStatus(),
                request.getProcess(),
                request.isAllowsQueueing()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateBusinessProcessSchedulerMessage(id);
    }
}
