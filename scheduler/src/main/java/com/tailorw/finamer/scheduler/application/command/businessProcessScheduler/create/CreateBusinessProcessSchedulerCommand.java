package com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateBusinessProcessSchedulerCommand implements ICommand {

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
    private UUID process;
    private boolean allowsQueueing;
    private String startTime;
    private String endTime;

    public CreateBusinessProcessSchedulerCommand(UUID frequency,
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
                                                 UUID process,
                                                 boolean allowsQueueing,
                                                 String startTime,
                                                 String endTime){
        this.id = UUID.randomUUID();
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
        this.process = process;
        this.allowsQueueing = allowsQueueing;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static CreateBusinessProcessSchedulerCommand fromRequest(CreateBusinessProcessSchedulerRequest request){
        return new CreateBusinessProcessSchedulerCommand(request.getFrequency(),
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
                request.getProcess(),
                request.isAllowsQueueing(),
                request.getStartTime(),
                request.getEndTime());
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateBusinessProcessSchedulerMessage(id);
    }
}
